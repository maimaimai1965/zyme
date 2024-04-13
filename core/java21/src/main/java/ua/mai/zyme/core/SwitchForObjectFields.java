package ua.mai.zyme.core;

public class SwitchForObjectFields {

    static class Achievement {
        String activityType;
        String levelOrPrize; // e.g., "National", "First Prize", "Participation", etc.
        int hours; // For community service in social activities
        boolean hasWon; // For debate competition
    }

//    static double getFeeWaiver(Achievement achievement) {
//        switch (achievement) {
//// Unguarded Patterns
//            case Achievement { activityType: "Sports" } -> return 50.0;
//            case Achievement { activityType: "Sports", levelOrPrize: "State" } -> return 30.0;
//            case Achievement { activityType: "Essay", levelOrPrize: "First Prize" } -> return 25.0;
//            case Achievement { activityType: "Model Construction", levelOrPrize: "Outstanding" } -> return 15.0;
//// Guarded Patterns
//            case Achievement { activityType: "Sports", levelOrPrize: "School" } when achievement.hasWon -> return 10.0;
//            case Achievement { activityType: "Debate" } when achievement.hasWon -> return 20.0;
//            case Achievement { activityType: "Debate", hasWon: false } -> return 5.0;
//            case Achievement { activityType: "Social Activities" } when achievement.hours > 100 -> return 40.0;
//            default -> return 0.0; // No fee waiver for other cases
//        }
//    }

}
