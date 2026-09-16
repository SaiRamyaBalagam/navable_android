package com.navable.app.util;

import com.navable.app.model.GuidanceMode;
import com.navable.app.model.RouteStep;

import java.util.List;

/**
 * Computes turn directions from route-point coordinates and builds
 * spoken guidance text for each {@link GuidanceMode}.
 */
public class NavigationGuide {

    public enum TurnDirection {
        STRAIGHT, SLIGHT_LEFT, SLIGHT_RIGHT, LEFT, RIGHT, SHARP_LEFT, SHARP_RIGHT
    }

    /**
     * Signed turn angle in degrees at {@code curr}, comparing the incoming
     * vector (prev->curr) to the outgoing vector (curr->next).
     * Positive = left turn, negative = right turn, assuming x increases to
     * the right and y increases "forward/up" on the floor plan.
     */
    public static double turnAngleDegrees(RouteStep prev, RouteStep curr, RouteStep next) {
        double inX = curr.getXCoordinate() - prev.getXCoordinate();
        double inY = curr.getYCoordinate() - prev.getYCoordinate();
        double outX = next.getXCoordinate() - curr.getXCoordinate();
        double outY = next.getYCoordinate() - curr.getYCoordinate();

        double cross = inX * outY - inY * outX;
        double dot = inX * outX + inY * outY;
        return Math.toDegrees(Math.atan2(cross, dot));
    }

    public static TurnDirection classify(double angleDegrees) {
        double abs = Math.abs(angleDegrees);
        boolean left = angleDegrees > 0;

        if (abs < 15) return TurnDirection.STRAIGHT;
        if (abs < 45) return left ? TurnDirection.SLIGHT_LEFT : TurnDirection.SLIGHT_RIGHT;
        if (abs < 135) return left ? TurnDirection.LEFT : TurnDirection.RIGHT;
        return left ? TurnDirection.SHARP_LEFT : TurnDirection.SHARP_RIGHT;
    }

    public static String label(TurnDirection direction) {
        switch (direction) {
            case SLIGHT_LEFT: return "slight left";
            case SLIGHT_RIGHT: return "slight right";
            case LEFT: return "left";
            case RIGHT: return "right";
            case SHARP_LEFT: return "sharp left";
            case SHARP_RIGHT: return "sharp right";
            default: return "straight";
        }
    }

    /** Builds the full spoken route text for the given guidance mode. */
    public static String buildSpokenRoute(List<RouteStep> steps, GuidanceMode mode) {
        StringBuilder sb = new StringBuilder();
        sb.append("Route found. Start at ").append(steps.get(0).getLocationName()).append(". ");

        int last = steps.size() - 1;
        for (int i = 1; i <= last; i++) {
            RouteStep step = steps.get(i);
            double distance = step.getDistanceFromPrevious();
            boolean isTurnPoint = i < last; // has both an incoming and outgoing segment
            TurnDirection direction = null;
            if (isTurnPoint) {
                double angle = turnAngleDegrees(steps.get(i - 1), step, steps.get(i + 1));
                direction = classify(angle);
            }

            switch (mode) {
                case MINIMAL:
                    if (isTurnPoint && direction != TurnDirection.STRAIGHT) {
                        sb.append("Turn ").append(label(direction)).append(" ahead. ");
                    }
                    break;

                case STANDARD:
                    if (i == last) {
                        sb.append("In ").append(String.format("%.0f", distance))
                          .append(" meters, you will arrive at ").append(step.getLocationName()).append(". ");
                    } else if (direction != TurnDirection.STRAIGHT) {
                        sb.append("In ").append(String.format("%.0f", distance))
                          .append(" meters, turn ").append(label(direction)).append(". ");
                    } else {
                        sb.append("Continue for ").append(String.format("%.0f", distance)).append(" meters. ");
                    }
                    break;

                case PRECISION:
                default:
                    sb.append("Walk straight for ").append(String.format("%.0f", distance)).append(" meters. ");
                    if (isTurnPoint && direction != TurnDirection.STRAIGHT) {
                        sb.append("Turn ").append(label(direction)).append(" now. ");
                    }
                    break;
            }
        }

        sb.append("You have arrived at your destination.");
        return sb.toString();
    }
}
