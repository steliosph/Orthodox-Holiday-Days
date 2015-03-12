package com.stelios.holidays;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class OrthodoxHolidays {

	private final int YEAR_TO_CALCULATE = 2025;

	private enum HolidayEnum {

		NEW_YEARS_DAY("New Years", new GregorianCalendar(2015, 01, 01)),

		EPIPHANY_DAY("Epiphany Day", new GregorianCalendar(2015, 01, 06)),

		GREEK_INDEPENDANCE_DAY("Greece Independance day",
				new GregorianCalendar(2015, 03, 25)),

		CYPRUS_NATIONAL_DAY("Cyprus National Day", new GregorianCalendar(2015,
				04, 01)),

		LABOUR_DAY("Labour Day", new GregorianCalendar(2015, 05, 01)),

		ASSUMPTION_DAY("Assumption Day", new GregorianCalendar(2015, 8, 15)),

		CYPRUS_INDEPENDENCE_DAY("Cyprus Independence Day",
				new GregorianCalendar(2015, 10, 01)),

		GREEK_NATIONAL_DAY("Greece National Day", new GregorianCalendar(2015,
				10, 28)),

		CHRISTMAS_DAY("Christams Day", new GregorianCalendar(2015, 12, 25)),

		BOXING_DAY("Boxing Day", new GregorianCalendar(2015, 12, 26)),

		GOOD_FRIDAY("Good Friday", -2),

		ORTHODOX_EASTER_MONDAY("Orthodox Easter Monday", 1),

		ORTHODOX_EASTER_TUESDAY("Orthodox Easter tuesday ", 2),

		ORTHODOX_WHIT_MONDAY("Orthodox Whit Monday", 50);

		private final String name_;
		private final Calendar staticDate_;
		private final Integer dayAfterEaster_;

		private HolidayEnum(String name, Calendar staticDate) {
			name_ = name;
			staticDate_ = staticDate;
			dayAfterEaster_ = null;
		}

		private HolidayEnum(String name, Integer dayAfterEaster) {
			name_ = name;
			staticDate_ = null;
			dayAfterEaster_ = dayAfterEaster;
		}

		public String getName() {
			return name_;
		}

		public Calendar getCalendar() {
			return staticDate_;
		}

		public Integer getDayAfterEaster() {
			return dayAfterEaster_;
		}
	}

	private Calendar calendar_ = new GregorianCalendar();

	public static void main(String[] args) {
		new OrthodoxHolidays().build();
		System.exit(0);
	}

	public void build() {
		GregorianCalendar orthodox = new GregorianCalendar();
		orthodox.setGregorianChange(new Date(Long.MAX_VALUE));
		calendar_ = orthodox;

		Date date = new Date();
		calendar_.setTime(date);

		for (int year = 2015; year <= YEAR_TO_CALCULATE; year++) {
			calendar_.set(Calendar.YEAR, year);
			addStaticHolidays();
			calculateGoodFriday();
			calculateEasterMonday();
			calculateEasterTuesday();
			calculateWhitMonday();
		}

	}

	private void addStaticHolidays() {
		addYearAndAddToList(HolidayEnum.NEW_YEARS_DAY);
		addYearAndAddToList(HolidayEnum.EPIPHANY_DAY);
		addYearAndAddToList(HolidayEnum.GREEK_INDEPENDANCE_DAY);
		addYearAndAddToList(HolidayEnum.CYPRUS_NATIONAL_DAY);
		addYearAndAddToList(HolidayEnum.LABOUR_DAY);
		addYearAndAddToList(HolidayEnum.ASSUMPTION_DAY);
		addYearAndAddToList(HolidayEnum.CYPRUS_INDEPENDENCE_DAY);
		addYearAndAddToList(HolidayEnum.GREEK_NATIONAL_DAY);
		addYearAndAddToList(HolidayEnum.CHRISTMAS_DAY);
		addYearAndAddToList(HolidayEnum.BOXING_DAY);
	}

	private void addYearAndAddToList(HolidayEnum holidayEnum) {
		int year = calendar_.get(Calendar.YEAR);
		Calendar cal = holidayEnum.getCalendar();
		cal.set(Calendar.YEAR, year);
		// Need to remove a month as months start from 0
		cal.set(Calendar.MONTH, cal.get(Calendar.MONTH) - 1);
		addToList(cal, holidayEnum.getName());
	}

	private void calculateGoodFriday() {
		// Good Fridays is two days before the easter.
		calculate(HolidayEnum.GOOD_FRIDAY);
	}

	private void calculateEasterMonday() {
		// Easter monday is a day after easter
		calculate(HolidayEnum.ORTHODOX_EASTER_MONDAY);
	}

	private void calculateEasterTuesday() {
		// Easter tuesday is 2 days after easter
		calculate(HolidayEnum.ORTHODOX_EASTER_TUESDAY);
	}

	private void calculateWhitMonday() {
		// Whit Monday is 50 days after easter
		calculate(HolidayEnum.ORTHODOX_WHIT_MONDAY);
	}

	private void calculate(HolidayEnum holidayEnum) {
		Calendar cal = calculateEasterFormula(holidayEnum);
		addToList(cal, holidayEnum.getName());
	}

	private void addToList(Calendar cal, String name) {
		System.out.println(cal.getTime() + " " + name);
	}

	private Calendar calculateEasterFormula(HolidayEnum holidayEnum) {
		synchronized (calendar_) {
			int year = calendar_.get(Calendar.YEAR);
			int g = year % 19; // "Golden Number" of year - 1
			int i = 0; // # of days from 3/21 to the Paschal full moon
			int j = 0; // Weekday (0-based) of Paschal full moon

			if (calendar_.getTime().after(
					((GregorianCalendar) calendar_).getGregorianChange())) {
				// We're past the Gregorian switchover, so use the Gregorian
				// rules.
				int c = year / 100;
				int h = (c - c / 4 - (8 * c + 13) / 25 + 19 * g + 15) % 30;
				i = h - (h / 28)
						* (1 - (h / 28) * (29 / (h + 1)) * ((21 - g) / 11));
				j = (year + year / 4 + i + 2 - c + c / 4) % 7;
			} else {
				// Use the old Julian rules.
				i = (19 * g + 15) % 30;
				j = (year + year / 4 + i) % 7;
			}
			int l = i - j;
			int m = 3 + (l + 40) / 44; // 1-based month in which Easter falls
			int d = l + 28 - 31 * (m / 4); // Date of Easter within that month

			calendar_.clear();
			calendar_.set(Calendar.ERA, GregorianCalendar.AD);
			calendar_.set(Calendar.YEAR, year);
			calendar_.set(Calendar.MONTH, m - 1); // 0-based
			calendar_.set(Calendar.DATE, (d + holidayEnum.getDayAfterEaster()));

			return calendar_;
		}
	}
