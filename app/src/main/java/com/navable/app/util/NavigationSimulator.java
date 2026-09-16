package com.navable.app.util;

import com.navable.app.model.GuidanceMode;
import com.navable.app.model.RouteStep;

import java.util.List;

/**
 * Drives simulated walking progress along a route, re-announcing guidance
 * as the (simulated) walker crosses waypoints. This stands in for real
 * indoor positioning (Phase 4 defers actual positioning hardware) — the
 * caller feeds it elapsed distance via {@link #advance(double)}, and it
 * reports progress and announcements through the {@link Listener}.
 */
public class NavigationSimulator {

    public interface Listener {
        /** Called whenever progress changes. distances are in meters. */
        void onProgress(int currentStepIndex, double distanceIntoSegment, double distanceRemainingInSegment, double totalDistanceRemaining);

        /** Called when new guidance text should be spoken/shown. */
        void onAnnouncement(String text);

        void onArrived();
    }

    private final List<RouteStep> steps;
    private final GuidanceMode mode;
    private final Listener listener;
    private final double totalDistance;

    private int segmentIndex = 1; // walking toward steps[segmentIndex]
    private double distanceIntoSegment = 0;
    private boolean segmentAnnounced = false;
    private boolean arrived = false;

    public NavigationSimulator(List<RouteStep> steps, GuidanceMode mode, Listener listener) {
        this.steps = steps;
        this.mode = mode;
        this.listener = listener;

        double total = 0;
        for (int i = 1; i < steps.size(); i++) {
            total += steps.get(i).getDistanceFromPrevious();
        }
        this.totalDistance = total;
    }

    /** Announces the first segment. Call once before the first advance(). */
    public void start() {
        announceSegmentStart();
        reportProgress();
    }

    /** Advances the simulated walker by the given distance in meters. */
    public void advance(double metersAdvanced) {
        if (arrived) return;

        distanceIntoSegment += metersAdvanced;
        double segmentLength = steps.get(segmentIndex).getDistanceFromPrevious();

        while (!arrived && distanceIntoSegment >= segmentLength) {
            distanceIntoSegment -= segmentLength;
            arriveAtWaypoint();
            if (arrived) break;
            segmentLength = steps.get(segmentIndex).getDistanceFromPrevious();
        }

        reportProgress();
    }

    private void arriveAtWaypoint() {
        int last = steps.size() - 1;
        boolean isTurnPoint = segmentIndex < last;

        if (isTurnPoint) {
            double angle = NavigationGuide.turnAngleDegrees(steps.get(segmentIndex - 1), steps.get(segmentIndex), steps.get(segmentIndex + 1));
            NavigationGuide.TurnDirection direction = NavigationGuide.classify(angle);
            if (direction != NavigationGuide.TurnDirection.STRAIGHT) {
                listener.onAnnouncement("Turn " + NavigationGuide.label(direction) + " now.");
            }
            segmentIndex++;
            segmentAnnounced = false;
            announceSegmentStart();
        } else {
            listener.onAnnouncement("You have arrived at " + steps.get(last).getLocationName() + ".");
            arrived = true;
            listener.onArrived();
        }
    }

    private void announceSegmentStart() {
        if (segmentAnnounced || mode == GuidanceMode.MINIMAL) {
            segmentAnnounced = true;
            return;
        }
        segmentAnnounced = true;

        int last = steps.size() - 1;
        RouteStep target = steps.get(segmentIndex);
        double distance = target.getDistanceFromPrevious();

        if (segmentIndex == last) {
            listener.onAnnouncement("In " + String.format("%.0f", distance) + " meters, you will arrive at " + target.getLocationName() + ".");
            return;
        }

        double angle = NavigationGuide.turnAngleDegrees(steps.get(segmentIndex - 1), target, steps.get(segmentIndex + 1));
        NavigationGuide.TurnDirection direction = NavigationGuide.classify(angle);

        if (mode == GuidanceMode.PRECISION) {
            listener.onAnnouncement("Walk straight for " + String.format("%.0f", distance) + " meters.");
        } else if (direction != NavigationGuide.TurnDirection.STRAIGHT) {
            listener.onAnnouncement("In " + String.format("%.0f", distance) + " meters, turn " + NavigationGuide.label(direction) + ".");
        }
    }

    private void reportProgress() {
        double segmentLength = arrived ? 0 : steps.get(segmentIndex).getDistanceFromPrevious();
        double remainingInSegment = Math.max(0, segmentLength - distanceIntoSegment);

        double remainingTotal = remainingInSegment;
        for (int i = segmentIndex + 1; i < steps.size(); i++) {
            remainingTotal += steps.get(i).getDistanceFromPrevious();
        }

        listener.onProgress(segmentIndex, distanceIntoSegment, remainingInSegment, remainingTotal);
    }

    public double getTotalDistance() {
        return totalDistance;
    }

    public boolean isArrived() {
        return arrived;
    }
}
