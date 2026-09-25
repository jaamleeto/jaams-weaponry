package net.jaams.weaponry.condition;

public record ConditionResult(boolean pass, String reason) {

    public static final ConditionResult PASS = new ConditionResult(true, "ok");

    public static ConditionResult passResult() {
        return PASS;
    }

    public static ConditionResult fail(String reason) {
        return new ConditionResult(false, reason);
    }

    public static ConditionResult of(boolean pass, String failReason) {
        return pass ? PASS : fail(failReason);
    }
}
