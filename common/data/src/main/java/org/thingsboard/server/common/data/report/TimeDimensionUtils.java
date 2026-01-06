package org.thingsboard.server.common.data.report;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;

/**
 * 时间维度转换工具类
 *
 * @author wwzl
 * @date 2025-01-17
 */
public class TimeDimensionUtils {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * 时间维度枚举
     */
    public enum TimeDimension {
        TODAY("今日"),
        YESTERDAY("昨日"),
        THIS_WEEK("本周"),
        THIS_WEEK_1("周"),
        LAST_WEEK_PERIOD("上周"),
        THIS_MONTH("本月"),
        LAST_MONTH_PERIOD("上月"),
        THIS_QUARTER("本季"),
        LAST_QUARTER_PERIOD("上季"),
        LAST_WEEK("近一周"),
        LAST_ONE_WEEK("近1周"),
        LAST_FIVE_DAYS("近5日"),
        LAST_5_DAYS("日"),
        LAST_MONTH("近一个月"),
        LAST_ONE_MONTH("近1个月"),
        LAST_THREE_MONTHS("近三个月"),
        LAST_3_MONTHS("近3个月"),
        LAST_FIVE_MONTHS("近5月"),
        LAST_5_MONTHS("月"),
        LAST_SIX_MONTHS("近六个月"),
        LAST_6_MONTHS("近6个月"),
        LAST_HALF_YEARS("近半年"),
        LAST_TWELVE_MONTHS("近十二个月"),
        LAST_12_MONTHS("近12个月"),
        LAST_ONE_YEAR("近一年"),
        LAST_1_YEAR("近1年"),
        BY_MONTH("按月"),
        BY_QUARTER("按季"),
        CUSTOM("自定义");

        private final String description;

        TimeDimension(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }

        public static TimeDimension fromString(String timeDimension) {
            if (timeDimension == null) {
                return null;
            }
            for (TimeDimension dimension : TimeDimension.values()) {
                if (dimension.name().equalsIgnoreCase(timeDimension) ||
                        dimension.getDescription().equals(timeDimension)) {
                    return dimension;
                }
            }
            return null;
        }
    }

    /**
     * 时间范围结果类
     */
    public static class TimeRange {
        private String startDate;
        private String endDate;
        private String startDateTime;
        private String endDateTime;

        public TimeRange(String startDate, String endDate) {
            this.startDate = startDate;
            this.endDate = endDate;
            this.startDateTime = startDate + " 00:00:00";
            this.endDateTime = endDate + " 23:59:59";
        }

        public String getStartDate() {
            return startDate;
        }

        public String getEndDate() {
            return endDate;
        }

        public String getStartDateTime() {
            return startDateTime;
        }

        public String getEndDateTime() {
            return endDateTime;
        }
    }

    /**
     * 根据时间维度获取时间范围
     *
     * @param timeDimension 时间维度
     * @param customStartDate 自定义开始日期（当timeDimension为CUSTOM时使用）
     * @param customEndDate 自定义结束日期（当timeDimension为CUSTOM时使用）
     * @return 时间范围
     */
    public static TimeRange getTimeRange(String timeDimension, String customStartDate, String customEndDate) {
        TimeDimension dimension = TimeDimension.fromString(timeDimension);

        if (dimension == null) {
            throw new IllegalArgumentException("不支持的时间维度: " + timeDimension);
        }

        LocalDate today = LocalDate.now();

        switch (dimension) {
            case TODAY:
                return new TimeRange(
                        today.format(DATE_FORMATTER),
                        today.format(DATE_FORMATTER)
                );
            case YESTERDAY:
                LocalDate yesterday = today.minusDays(1);
                return new TimeRange(
                        yesterday.format(DATE_FORMATTER),
                        yesterday.format(DATE_FORMATTER)
                );
            case THIS_WEEK_1:
            case THIS_WEEK:
                LocalDate weekStart = today.with(TemporalAdjusters.previousOrSame(java.time.DayOfWeek.MONDAY));
                LocalDate weekEnd = today.with(TemporalAdjusters.nextOrSame(java.time.DayOfWeek.SUNDAY));
                return new TimeRange(
                        weekStart.format(DATE_FORMATTER),
                        weekEnd.format(DATE_FORMATTER)
                );
            case LAST_WEEK_PERIOD:
                // 上周：当前周的周一/周日各减一周
                LocalDate currWeekStart = today.with(TemporalAdjusters.previousOrSame(java.time.DayOfWeek.MONDAY));
                LocalDate currWeekEnd = today.with(TemporalAdjusters.nextOrSame(java.time.DayOfWeek.SUNDAY));
                LocalDate lastWeekStart = currWeekStart.minusWeeks(1);
                LocalDate lastWeekEnd = currWeekEnd.minusWeeks(1);
                return new TimeRange(
                        lastWeekStart.format(DATE_FORMATTER),
                        lastWeekEnd.format(DATE_FORMATTER)
                );

            case THIS_MONTH:
                LocalDate monthStart = today.with(TemporalAdjusters.firstDayOfMonth());
                LocalDate monthEnd = today.with(TemporalAdjusters.lastDayOfMonth());
                return new TimeRange(
                        monthStart.format(DATE_FORMATTER),
                        monthEnd.format(DATE_FORMATTER)
                );
            case LAST_MONTH_PERIOD:
                // 上月：上月第一天到上月最后一天
                LocalDate prevMonthStart = today.minusMonths(1).with(TemporalAdjusters.firstDayOfMonth());
                LocalDate prevMonthEnd = today.minusMonths(1).with(TemporalAdjusters.lastDayOfMonth());
                return new TimeRange(
                        prevMonthStart.format(DATE_FORMATTER),
                        prevMonthEnd.format(DATE_FORMATTER)
                );

            case THIS_QUARTER:
                int currentQuarter = (today.getMonthValue() - 1) / 3 + 1;
                LocalDate quarterStart = today.withMonth((currentQuarter - 1) * 3 + 1)
                        .with(TemporalAdjusters.firstDayOfMonth());
                LocalDate quarterEnd = today.withMonth(currentQuarter * 3)
                        .with(TemporalAdjusters.lastDayOfMonth());
                return new TimeRange(
                        quarterStart.format(DATE_FORMATTER),
                        quarterEnd.format(DATE_FORMATTER)
                );
            case LAST_QUARTER_PERIOD:
                // 上季度：计算上一季度的首末日
                int currentQ = (today.getMonthValue() - 1) / 3 + 1;
                int prevQ = currentQ - 1;
                int year = today.getYear();
                if (prevQ == 0) {
                    prevQ = 4;
                    year = year - 1;
                }
                LocalDate prevQuarterStart = LocalDate.of(year, (prevQ - 1) * 3 + 1, 1)
                        .with(TemporalAdjusters.firstDayOfMonth());
                LocalDate prevQuarterEnd = LocalDate.of(year, prevQ * 3, 1)
                        .with(TemporalAdjusters.lastDayOfMonth());
                return new TimeRange(
                        prevQuarterStart.format(DATE_FORMATTER),
                        prevQuarterEnd.format(DATE_FORMATTER)
                );

            case LAST_WEEK:
                // 近一周：从7天前到今天
                LocalDate lastWeekStart1 = today.minusDays(6);
                return new TimeRange(
                        lastWeekStart1.format(DATE_FORMATTER),
                        today.format(DATE_FORMATTER)
                );

            case LAST_MONTH:
                // 近一个月：从1个月前到今天
                LocalDate lastMonthStart = today.minusMonths(1).with(TemporalAdjusters.firstDayOfMonth());
                return new TimeRange(
                        lastMonthStart.format(DATE_FORMATTER),
                        today.format(DATE_FORMATTER)
                );

            case LAST_THREE_MONTHS:
                // 近三个月：从3个月前到今天
                LocalDate lastThreeMonthsStart = today.minusMonths(3).with(TemporalAdjusters.firstDayOfMonth());
                return new TimeRange(
                        lastThreeMonthsStart.format(DATE_FORMATTER),
                        today.format(DATE_FORMATTER)
                );

            case LAST_SIX_MONTHS:
                // 近六个月：从6个月前到今天
                LocalDate lastSixMonthsStart = today.minusMonths(6).with(TemporalAdjusters.firstDayOfMonth());
                return new TimeRange(
                        lastSixMonthsStart.format(DATE_FORMATTER),
                        today.format(DATE_FORMATTER)
                );

            case LAST_ONE_WEEK:
                // 近1周：从7天前到今天（与近一周相同）
                LocalDate lastOneWeekStart = today.minusDays(6);
                return new TimeRange(
                        lastOneWeekStart.format(DATE_FORMATTER),
                        today.format(DATE_FORMATTER)
                );

            case LAST_FIVE_DAYS:
            case LAST_5_DAYS:
                // 近5日/近5天：从5天前到今天
                LocalDate lastFiveDaysStart = today.minusDays(4);
                return new TimeRange(
                        lastFiveDaysStart.format(DATE_FORMATTER),
                        today.format(DATE_FORMATTER)
                );

            case LAST_ONE_MONTH:
                // 近1个月：从1个月前到今天（与近一个月相同）
                LocalDate lastOneMonthStart = today.minusMonths(1).with(TemporalAdjusters.firstDayOfMonth());
                return new TimeRange(
                        lastOneMonthStart.format(DATE_FORMATTER),
                        today.format(DATE_FORMATTER)
                );

            case LAST_3_MONTHS:
                // 近3个月：从3个月前到今天（与近三个月相同）
                LocalDate last3MonthsStart = today.minusMonths(3).with(TemporalAdjusters.firstDayOfMonth());
                return new TimeRange(
                        last3MonthsStart.format(DATE_FORMATTER),
                        today.format(DATE_FORMATTER)
                );

            case LAST_FIVE_MONTHS:
            case LAST_5_MONTHS:
                // 近5月/近5个月：从5个月前到今天
                LocalDate last5MonthsStart = today.minusMonths(5).with(TemporalAdjusters.firstDayOfMonth());
                return new TimeRange(
                        last5MonthsStart.format(DATE_FORMATTER),
                        today.format(DATE_FORMATTER)
                );

            case LAST_6_MONTHS:
            case LAST_HALF_YEARS:
                // 近6个月：从6个月前到今天（与近六个月相同）
                LocalDate last6MonthsStart = today.minusMonths(6).with(TemporalAdjusters.firstDayOfMonth());
                return new TimeRange(
                        last6MonthsStart.format(DATE_FORMATTER),
                        today.format(DATE_FORMATTER)
                );

            case LAST_TWELVE_MONTHS:
            case LAST_12_MONTHS:
            case LAST_ONE_YEAR:
            case LAST_1_YEAR:
                // 近十二个月/近12个月/近一年/近1年：从12个月前到今天
                LocalDate lastTwelveMonthsStart = today.minusMonths(12).with(TemporalAdjusters.firstDayOfMonth());
                return new TimeRange(
                        lastTwelveMonthsStart.format(DATE_FORMATTER),
                        today.format(DATE_FORMATTER)
                );

            case BY_MONTH:
                // 按月：最近12个月，从12个月前到今天
                LocalDate byMonthStart = today.minusMonths(11).with(TemporalAdjusters.firstDayOfMonth());
                return new TimeRange(
                        byMonthStart.format(DATE_FORMATTER),
                        today.format(DATE_FORMATTER)
                );

            case BY_QUARTER:
                // 按季：最近4个季度，从4个季度前到今天
                LocalDate byQuarterStart = today.minusMonths(11).with(TemporalAdjusters.firstDayOfMonth());
                return new TimeRange(
                        byQuarterStart.format(DATE_FORMATTER),
                        today.format(DATE_FORMATTER)
                );

            case CUSTOM:
                if (customStartDate == null || customEndDate == null) {
                    throw new IllegalArgumentException("自定义时间维度必须提供开始日期和结束日期");
                }
                // 验证日期格式
                try {
                    LocalDate.parse(customStartDate, DATE_FORMATTER);
                    LocalDate.parse(customEndDate, DATE_FORMATTER);
                } catch (Exception e) {
                    throw new IllegalArgumentException("日期格式错误，请使用 yyyy-MM-dd 格式");
                }
                return new TimeRange(customStartDate, customEndDate);

            default:
                throw new IllegalArgumentException("不支持的时间维度: " + timeDimension);
        }
    }

    /**
     * 根据时间维度获取时间范围（不包含自定义）
     *
     * @param timeDimension 时间维度
     * @return 时间范围
     */
    public static TimeRange getTimeRange(String timeDimension) {
        return getTimeRange(timeDimension, null, null);
    }

    /**
     * 验证自定义时间范围是否有效
     *
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 是否有效
     */
    public static boolean isValidCustomTimeRange(String startDate, String endDate) {
        if (startDate == null || endDate == null) {
            return false;
        }

        try {
            LocalDate start = LocalDate.parse(startDate, DATE_FORMATTER);
            LocalDate end = LocalDate.parse(endDate, DATE_FORMATTER);
            return !start.isAfter(end);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 获取时间维度的描述
     *
     * @param timeDimension 时间维度
     * @return 描述
     */
    public static String getTimeDimensionDescription(String timeDimension) {
        TimeDimension dimension = TimeDimension.fromString(timeDimension);
        return dimension != null ? dimension.getDescription() : timeDimension;
    }

    /**
     * 处理时间维度并设置到DTO中
     * 封装时间维度处理逻辑，避免代码重复
     *
     * @param timeDimension 时间维度
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 处理后的时间范围
     */
    public static TimeRange processTimeDimension(String timeDimension, java.util.Date startDate, java.util.Date endDate) {
        // 使用时间维度工具类获取时间范围
        TimeRange timeRange = getTimeRange(timeDimension,
                startDate != null ? startDate.toString() : null,
                endDate != null ? endDate.toString() : null);

        return timeRange;
    }

    /**
     * 处理时间维度并设置到DTO中（带空值检查）
     * 封装时间维度处理逻辑，避免代码重复
     *
     * @param dto 包含时间维度信息的DTO对象
     * @param timeDimension 时间维度
     * @param startDate 开始日期
     * @param endDate 结束日期
     */
    public static void processAndSetTimeRange(Object dto, String timeDimension, java.util.Date startDate, java.util.Date endDate) {
        // 格式化日期为 yyyy-MM-dd 格式
        String startDateStr = null;
        String endDateStr = null;

        if (startDate != null) {
            startDateStr = new java.text.SimpleDateFormat("yyyy-MM-dd").format(startDate);
        }
        if (endDate != null) {
            endDateStr = new java.text.SimpleDateFormat("yyyy-MM-dd").format(endDate);
        }

        // 使用时间维度工具类获取时间范围
        TimeRange timeRange = getTimeRange(timeDimension, startDateStr, endDateStr);

        // 设置计算后的时间范围到DTO（增加空值检查）
        try {
            if (timeRange.getStartDate() != null) {
                java.lang.reflect.Method setStartDateMethod = dto.getClass().getMethod("setStartDate", java.util.Date.class);
                setStartDateMethod.invoke(dto, java.sql.Date.valueOf(timeRange.getStartDate()));
            }
            if (timeRange.getEndDate() != null) {
                java.lang.reflect.Method setEndDateMethod = dto.getClass().getMethod("setEndDate", java.util.Date.class);
                setEndDateMethod.invoke(dto, java.sql.Date.valueOf(timeRange.getEndDate()));
            }
        } catch (Exception e) {
            throw new RuntimeException("设置时间范围到DTO失败", e);
        }
    }
}

