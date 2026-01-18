package managers;
import models.*;

public class CalculatePrice {
    private static final double BASE_FARE = 1000.0;
    private static final double KM_RATE = 2.0;
    private static final double BAGGAGE_EXCESS_FEE = 100.0; // kg başı ceza

    public static double calculateBasicPrice(Route route, TicketClass type) {
        double distance = route.getDistance();
        double basePrice = BASE_FARE + (distance * KM_RATE);
        return basePrice * type.getPriceMultiplier();
    }

    // Bagaj aşım ücretini hesaplar
    public static double calculateExcessFee(double totalAllowance, double actualWeight) {
        if (actualWeight > totalAllowance) {
            return (actualWeight - totalAllowance) * BAGGAGE_EXCESS_FEE;
        }
        return 0.0;
    }

    // Sadakat puanının TL karşılığını hesaplar (10 Puan = 1 TL)
    public static double calculateLoyaltyDiscount(int points) {
        return points / 10.0;
    }
}

