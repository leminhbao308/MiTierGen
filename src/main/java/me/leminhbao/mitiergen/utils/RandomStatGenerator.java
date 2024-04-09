package me.leminhbao.mitiergen.utils;

import lombok.Getter;
import me.leminhbao.mitiergen.config.ConfigConstants;
import net.Indyuce.mmoitems.api.item.build.MMOItemBuilder;
import net.Indyuce.mmoitems.stat.data.DoubleData;
import net.Indyuce.mmoitems.stat.data.random.RandomStatData;
import net.Indyuce.mmoitems.stat.data.type.StatData;

import java.util.Random;

public class RandomStatGenerator implements RandomStatData {

    @Getter
    private double minPercent, maxPercent;
    private static final Random random = new Random();

    public static final RandomStatGenerator ZERO = new RandomStatGenerator(0, 0);

    public RandomStatGenerator(double minPercent, double maxPercent) {
        this.minPercent = minPercent;
        this.maxPercent = maxPercent;
    }

    @Override
    public DoubleData randomize(MMOItemBuilder mmoItemBuilder) {
        return new DoubleData(calculate(mmoItemBuilder.getLevel(), ConfigConstants.DATA_TYPE_PERCENT));
    }

    public DoubleData randomize(StatData x, String dataType) {
        if (x instanceof DoubleData) {

            return new DoubleData(calculate(((DoubleData) x).getValue(), dataType));
        }
        return new DoubleData(calculate(1, dataType));
    }

    public double calculate(double x, String dataType) {
        switch (dataType) {
            case ConfigConstants.DATA_TYPE_PERCENT:
                return x * (1 + (random.nextDouble() * (maxPercent - minPercent) + minPercent) / 100);
            case ConfigConstants.DATA_TYPE_NUMBER:
                return x + random.nextDouble() * (maxPercent - minPercent) + minPercent;
            default:
                return x;
        }
    }
}
