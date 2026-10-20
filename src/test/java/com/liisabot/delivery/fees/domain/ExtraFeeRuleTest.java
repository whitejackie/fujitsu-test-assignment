package com.liisabot.delivery.fees.domain;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class ExtraFeeRuleTest {

    @Test
    void betweenMinusTenAndZeroIncludesBothEnds() {
        ExtraFeeRule rule = new ExtraFeeRule();
        rule.setMinValue(-10.0);
        rule.setMinInclusive(true);
        rule.setMaxValue(0.0);
        rule.setMaxInclusive(true);

        assertTrue(rule.matchesNumber(-10.0));
        assertTrue(rule.matchesNumber(-2.1));
        assertTrue(rule.matchesNumber(0.0));
        assertFalse(rule.matchesNumber(-10.1));
        assertFalse(rule.matchesNumber(0.1));
    }

    @Test
    void belowMinusTenDoesNotIncludeMinusTen() {
        ExtraFeeRule rule = new ExtraFeeRule();
        rule.setMaxValue(-10.0);
        rule.setMaxInclusive(false);

        assertTrue(rule.matchesNumber(-10.1));
        assertFalse(rule.matchesNumber(-10.0));
    }

    @Test
    void aboveTwentyDoesNotIncludeTwenty() {
        ExtraFeeRule rule = new ExtraFeeRule();
        rule.setMinValue(20.0);
        rule.setMinInclusive(false);

        assertTrue(rule.matchesNumber(20.1));
        assertFalse(rule.matchesNumber(20.0));
    }

    @Test
    void phenomenonMatchIgnoresCaseAndHandlesNull() {
        ExtraFeeRule rule = new ExtraFeeRule();
        rule.setMatchText("snow");

        assertTrue(rule.matchesPhenomenon("Light SnOw shower"));
        assertFalse(rule.matchesPhenomenon("Clear"));
        assertFalse(rule.matchesPhenomenon(null));
    }
}
