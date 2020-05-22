/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2020 TeamApps.org
 * ---
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * =========================LICENSE_END==================================
 */
package org.teamapps.ux.component.timegraph.partitioning;

import org.teamapps.ux.component.timegraph.TimeGraphZoomLevel;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

public enum TimePartitionUnit implements TimeGraphZoomLevel {
	MILLISECOND(1) {
		@Override
		public ZonedDateTime getPartition(ZonedDateTime zonedDateTime) {
			return zonedDateTime.truncatedTo(ChronoUnit.MILLIS);
		}

		@Override
		public ZonedDateTime increment(ZonedDateTime zonedDateTime) {
			return zonedDateTime.plus(1, ChronoUnit.MILLIS);
		}

		@Override
		public ZonedDateTime decrement(ZonedDateTime zonedDateTime) {
			return zonedDateTime.minus(1, ChronoUnit.MILLIS);
		}
	}, MILLISECOND_2(2) {
		@Override
		public ZonedDateTime getPartition(ZonedDateTime zonedDateTime) {
			long epochMilli = zonedDateTime.toInstant().toEpochMilli();
			epochMilli = epochMilli - epochMilli % 2;
			return ZonedDateTime.ofInstant(Instant.ofEpochMilli(epochMilli), zonedDateTime.getZone());
		}

		@Override
		public ZonedDateTime increment(ZonedDateTime zonedDateTime) {
			return zonedDateTime.plus(2, ChronoUnit.MILLIS);
		}

		@Override
		public ZonedDateTime decrement(ZonedDateTime zonedDateTime) {
			return zonedDateTime.minus(2, ChronoUnit.MILLIS);
		}
	}, MILLISECOND_5(5) {
		@Override
		public ZonedDateTime getPartition(ZonedDateTime zonedDateTime) {
			long epochMilli = zonedDateTime.toInstant().toEpochMilli();
			epochMilli = epochMilli - epochMilli % 5;
			return ZonedDateTime.ofInstant(Instant.ofEpochMilli(epochMilli), zonedDateTime.getZone());
		}

		@Override
		public ZonedDateTime increment(ZonedDateTime zonedDateTime) {
			return zonedDateTime.plus(5, ChronoUnit.MILLIS);
		}

		@Override
		public ZonedDateTime decrement(ZonedDateTime zonedDateTime) {
			return zonedDateTime.minus(5, ChronoUnit.MILLIS);
		}
	}, MILLISECOND_10(10) {
		@Override
		public ZonedDateTime getPartition(ZonedDateTime zonedDateTime) {
			long epochMilli = zonedDateTime.toInstant().toEpochMilli();
			epochMilli = epochMilli - epochMilli % 10;
			return ZonedDateTime.ofInstant(Instant.ofEpochMilli(epochMilli), zonedDateTime.getZone());
		}

		@Override
		public ZonedDateTime increment(ZonedDateTime zonedDateTime) {
			return zonedDateTime.plus(10, ChronoUnit.MILLIS);
		}

		@Override
		public ZonedDateTime decrement(ZonedDateTime zonedDateTime) {
			return zonedDateTime.minus(10, ChronoUnit.MILLIS);
		}
	}, MILLISECOND_20(20) {
		@Override
		public ZonedDateTime getPartition(ZonedDateTime zonedDateTime) {
			long epochMilli = zonedDateTime.toInstant().toEpochMilli();
			epochMilli = epochMilli - epochMilli % 20;
			return ZonedDateTime.ofInstant(Instant.ofEpochMilli(epochMilli), zonedDateTime.getZone());
		}

		@Override
		public ZonedDateTime increment(ZonedDateTime zonedDateTime) {
			return zonedDateTime.plus(20, ChronoUnit.MILLIS);
		}

		@Override
		public ZonedDateTime decrement(ZonedDateTime zonedDateTime) {
			return zonedDateTime.minus(20, ChronoUnit.MILLIS);
		}
	}, MILLISECOND_50(50) {
		@Override
		public ZonedDateTime getPartition(ZonedDateTime zonedDateTime) {
			long epochMilli = zonedDateTime.toInstant().toEpochMilli();
			epochMilli = epochMilli - epochMilli % 50;
			return ZonedDateTime.ofInstant(Instant.ofEpochMilli(epochMilli), zonedDateTime.getZone());
		}

		@Override
		public ZonedDateTime increment(ZonedDateTime zonedDateTime) {
			return zonedDateTime.plus(50, ChronoUnit.MILLIS);
		}

		@Override
		public ZonedDateTime decrement(ZonedDateTime zonedDateTime) {
			return zonedDateTime.minus(50, ChronoUnit.MILLIS);
		}
	},	MILLISECOND_100(100) {
		@Override
		public ZonedDateTime getPartition(ZonedDateTime zonedDateTime) {
			long epochMilli = zonedDateTime.toInstant().toEpochMilli();
			epochMilli = epochMilli - epochMilli % 100;
			return ZonedDateTime.ofInstant(Instant.ofEpochMilli(epochMilli), zonedDateTime.getZone());
		}

		@Override
		public ZonedDateTime increment(ZonedDateTime zonedDateTime) {
			return zonedDateTime.plus(100, ChronoUnit.MILLIS);
		}

		@Override
		public ZonedDateTime decrement(ZonedDateTime zonedDateTime) {
			return zonedDateTime.minus(100, ChronoUnit.MILLIS);
		}
	},	MILLISECOND_200(200) {
		@Override
		public ZonedDateTime getPartition(ZonedDateTime zonedDateTime) {
			long epochMilli = zonedDateTime.toInstant().toEpochMilli();
			epochMilli = epochMilli - epochMilli % 200;
			return ZonedDateTime.ofInstant(Instant.ofEpochMilli(epochMilli), zonedDateTime.getZone());
		}

		@Override
		public ZonedDateTime increment(ZonedDateTime zonedDateTime) {
			return zonedDateTime.plus(200, ChronoUnit.MILLIS);
		}

		@Override
		public ZonedDateTime decrement(ZonedDateTime zonedDateTime) {
			return zonedDateTime.minus(200, ChronoUnit.MILLIS);
		}
	},	MILLISECOND_500(500) {
		@Override
		public ZonedDateTime getPartition(ZonedDateTime zonedDateTime) {
			long epochMilli = zonedDateTime.toInstant().toEpochMilli();
			epochMilli = epochMilli - epochMilli % 500;
			return ZonedDateTime.ofInstant(Instant.ofEpochMilli(epochMilli), zonedDateTime.getZone());
		}

		@Override
		public ZonedDateTime increment(ZonedDateTime zonedDateTime) {
			return zonedDateTime.plus(500, ChronoUnit.MILLIS);
		}

		@Override
		public ZonedDateTime decrement(ZonedDateTime zonedDateTime) {
			return zonedDateTime.minus(500, ChronoUnit.MILLIS);
		}
	},
	SECOND(1000) {
		@Override
		public ZonedDateTime getPartition(ZonedDateTime zonedDateTime) {
			return zonedDateTime.truncatedTo(ChronoUnit.SECONDS);
		}

		@Override
		public ZonedDateTime increment(ZonedDateTime zonedDateTime) {
			return zonedDateTime.plusSeconds(1);
		}

		@Override
		public ZonedDateTime decrement(ZonedDateTime zonedDateTime) {
			return zonedDateTime.minusSeconds(1);
		}
	},
	SECONDS_2(2000) {
		@Override
		public ZonedDateTime getPartition(ZonedDateTime zonedDateTime) {
			return zonedDateTime.truncatedTo(ChronoUnit.SECONDS).withSecond(2 * (zonedDateTime.getSecond() / 2));
		}

		@Override
		public ZonedDateTime increment(ZonedDateTime zonedDateTime) {
			return zonedDateTime.plusSeconds(2);
		}

		@Override
		public ZonedDateTime decrement(ZonedDateTime zonedDateTime) {
			return zonedDateTime.minusSeconds(2);
		}
	},
	SECONDS_5(5000) {
		@Override
		public ZonedDateTime getPartition(ZonedDateTime zonedDateTime) {
			return zonedDateTime.truncatedTo(ChronoUnit.SECONDS).withSecond(5 * (zonedDateTime.getSecond() / 5));
		}

		@Override
		public ZonedDateTime increment(ZonedDateTime zonedDateTime) {
			return zonedDateTime.plusSeconds(5);
		}

		@Override
		public ZonedDateTime decrement(ZonedDateTime zonedDateTime) {
			return zonedDateTime.minusSeconds(5);
		}
	},
	SECONDS_10(10_000) {
		@Override
		public ZonedDateTime getPartition(ZonedDateTime zonedDateTime) {
			return zonedDateTime.truncatedTo(ChronoUnit.SECONDS).withSecond(10 * (zonedDateTime.getSecond() / 10));
		}

		@Override
		public ZonedDateTime increment(ZonedDateTime zonedDateTime) {
			return zonedDateTime.plusSeconds(10);
		}

		@Override
		public ZonedDateTime decrement(ZonedDateTime zonedDateTime) {
			return zonedDateTime.minusSeconds(10);
		}
	},
	SECONDS_15(15_000) {
		@Override
		public ZonedDateTime getPartition(ZonedDateTime zonedDateTime) {
			return zonedDateTime.truncatedTo(ChronoUnit.SECONDS).withSecond(15 * (zonedDateTime.getSecond() / 15));
		}

		@Override
		public ZonedDateTime increment(ZonedDateTime zonedDateTime) {
			return zonedDateTime.plusSeconds(15);
		}

		@Override
		public ZonedDateTime decrement(ZonedDateTime zonedDateTime) {
			return zonedDateTime.minusSeconds(15);
		}
	},
	SECONDS_30(30_000) {
		@Override
		public ZonedDateTime getPartition(ZonedDateTime zonedDateTime) {
			return zonedDateTime.truncatedTo(ChronoUnit.SECONDS).withSecond(30 * (zonedDateTime.getSecond() / 30));
		}

		@Override
		public ZonedDateTime increment(ZonedDateTime zonedDateTime) {
			return zonedDateTime.plusSeconds(30);
		}

		@Override
		public ZonedDateTime decrement(ZonedDateTime zonedDateTime) {
			return zonedDateTime.minusSeconds(30);
		}
	},
	MINUTE(60_000) {
		@Override
		public ZonedDateTime getPartition(ZonedDateTime zonedDateTime) {
			return zonedDateTime.truncatedTo(ChronoUnit.MINUTES);
		}

		@Override
		public ZonedDateTime increment(ZonedDateTime zonedDateTime) {
			return zonedDateTime.plusMinutes(1);
		}

		@Override
		public ZonedDateTime decrement(ZonedDateTime zonedDateTime) {
			return zonedDateTime.minusMinutes(1);
		}
	},
	MINUTES_2(2 * 60_000) {
		@Override
		public ZonedDateTime getPartition(ZonedDateTime zonedDateTime) {
			return zonedDateTime.truncatedTo(ChronoUnit.MINUTES).withMinute(2 * (zonedDateTime.getMinute() / 2));
		}

		@Override
		public ZonedDateTime increment(ZonedDateTime zonedDateTime) {
			return zonedDateTime.plusMinutes(2);
		}

		@Override
		public ZonedDateTime decrement(ZonedDateTime zonedDateTime) {
			return zonedDateTime.minusMinutes(2);
		}
	},
	MINUTES_5(5 * 60_000) {
		@Override
		public ZonedDateTime getPartition(ZonedDateTime zonedDateTime) {
			return zonedDateTime.truncatedTo(ChronoUnit.MINUTES).withMinute(5 * (zonedDateTime.getMinute() / 5));
		}

		@Override
		public ZonedDateTime increment(ZonedDateTime zonedDateTime) {
			return zonedDateTime.plusMinutes(5);
		}

		@Override
		public ZonedDateTime decrement(ZonedDateTime zonedDateTime) {
			return zonedDateTime.minusMinutes(5);
		}
	},
	MINUTES_10(10 * 60_000) {
		@Override
		public ZonedDateTime getPartition(ZonedDateTime zonedDateTime) {
			return zonedDateTime.truncatedTo(ChronoUnit.MINUTES).withMinute(10 * (zonedDateTime.getMinute() / 10));
		}

		@Override
		public ZonedDateTime increment(ZonedDateTime zonedDateTime) {
			return zonedDateTime.plusMinutes(10);
		}

		@Override
		public ZonedDateTime decrement(ZonedDateTime zonedDateTime) {
			return zonedDateTime.minusMinutes(10);
		}
	},
	MINUTES_15(15 * 60_000) {
		@Override
		public ZonedDateTime getPartition(ZonedDateTime zonedDateTime) {
			return zonedDateTime.truncatedTo(ChronoUnit.MINUTES).withMinute(15 * (zonedDateTime.getMinute() / 15));
		}

		@Override
		public ZonedDateTime increment(ZonedDateTime zonedDateTime) {
			return zonedDateTime.plusMinutes(15);
		}

		@Override
		public ZonedDateTime decrement(ZonedDateTime zonedDateTime) {
			return zonedDateTime.minusMinutes(15);
		}
	},
	MINUTES_30(30 * 60_000) {
		@Override
		public ZonedDateTime getPartition(ZonedDateTime zonedDateTime) {
			return zonedDateTime.truncatedTo(ChronoUnit.MINUTES).withMinute(30 * (zonedDateTime.getMinute() / 30));
		}

		@Override
		public ZonedDateTime increment(ZonedDateTime zonedDateTime) {
			return zonedDateTime.plusMinutes(30);
		}

		@Override
		public ZonedDateTime decrement(ZonedDateTime zonedDateTime) {
			return zonedDateTime.minusMinutes(30);
		}
	},
	HOUR(3_600_000) {
		@Override
		public ZonedDateTime getPartition(ZonedDateTime zonedDateTime) {
			return zonedDateTime.truncatedTo(ChronoUnit.HOURS);
		}

		@Override
		public ZonedDateTime increment(ZonedDateTime zonedDateTime) {
			return zonedDateTime.plusHours(1);
		}

		@Override
		public ZonedDateTime decrement(ZonedDateTime zonedDateTime) {
			return zonedDateTime.minusHours(1);
		}
	},
	HOURS_6(6 * 3_600_000) {
		@Override
		public ZonedDateTime getPartition(ZonedDateTime zonedDateTime) {
			return zonedDateTime.truncatedTo(ChronoUnit.HOURS).withHour(6 * (zonedDateTime.getHour() / 6));
		}

		@Override
		public ZonedDateTime increment(ZonedDateTime zonedDateTime) {
			// make sure we are not the victims of a clock change - still not perfect...
			if (zonedDateTime.getHour() == 0) {
				return zonedDateTime.withHour(6);
			} else if (zonedDateTime.getHour() == 6) {
				return zonedDateTime.withHour(12);
			} else if (zonedDateTime.getHour() == 12) {
				return zonedDateTime.withHour(18);
			} else if (zonedDateTime.getHour() == 18) {
				return zonedDateTime.plusDays(1).withHour(0);
			} else {
				return zonedDateTime.plusHours(6);
			}
		}

		@Override
		public ZonedDateTime decrement(ZonedDateTime zonedDateTime) {
			// make sure we are not the victims of a clock change - still not perfect...
			if (zonedDateTime.getHour() == 0) {
				return zonedDateTime.minusDays(1).withHour(18);
			} else if (zonedDateTime.getHour() == 6) {
				return zonedDateTime.withHour(0);
			} else if (zonedDateTime.getHour() == 12) {
				return zonedDateTime.withHour(6);
			} else if (zonedDateTime.getHour() == 18) {
				return zonedDateTime.withHour(12);
			} else {
				return zonedDateTime.minusHours(6);
			}
		}
	},
	HOURS_12(12 * 3_600_000) {
		@Override
		public ZonedDateTime getPartition(ZonedDateTime zonedDateTime) {
			return zonedDateTime.truncatedTo(ChronoUnit.HOURS).withHour(12 * (zonedDateTime.getHour() / 12));
		}

		@Override
		public ZonedDateTime increment(ZonedDateTime zonedDateTime) {
			// make sure we are not the victims of a clock change - still not perfect...
			if (zonedDateTime.getHour() == 0) {
				return zonedDateTime.withHour(12);
			} else if (zonedDateTime.getHour() == 12) {
				return zonedDateTime.plusDays(1).withHour(0);
			} else {
				return zonedDateTime.plusHours(12);
			}
		}

		@Override
		public ZonedDateTime decrement(ZonedDateTime zonedDateTime) {
			// make sure we are not the victims of a clock change - still not perfect...
			if (zonedDateTime.getHour() == 0) {
				return zonedDateTime.minusDays(1).withHour(12);
			} else if (zonedDateTime.getHour() == 12) {
				return zonedDateTime.withHour(0);
			} else {
				return zonedDateTime.minusHours(12);
			}
		}
	},
	DAY(86_400_000) {
		@Override
		public ZonedDateTime getPartition(ZonedDateTime zonedDateTime) {
			return zonedDateTime.truncatedTo(ChronoUnit.DAYS);
		}

		@Override
		public ZonedDateTime increment(ZonedDateTime zonedDateTime) {
			return zonedDateTime.plusDays(1);
		}

		@Override
		public ZonedDateTime decrement(ZonedDateTime zonedDateTime) {
			return zonedDateTime.minusDays(1);
		}
	},
	WEEK_SUNDAY(7 * 86_400_000) {
		@Override
		public ZonedDateTime getPartition(ZonedDateTime zonedDateTime) {
			zonedDateTime = zonedDateTime.truncatedTo(ChronoUnit.DAYS);
			int dayOfWeek = zonedDateTime.getDayOfWeek().getValue();
			if (dayOfWeek < DayOfWeek.SUNDAY.getValue() /*7*/) {
				zonedDateTime = zonedDateTime.minusDays(dayOfWeek);
			}
			return zonedDateTime;
		}

		@Override
		public ZonedDateTime increment(ZonedDateTime zonedDateTime) {
			return zonedDateTime.plusDays(7);
		}

		@Override
		public ZonedDateTime decrement(ZonedDateTime zonedDateTime) {
			return zonedDateTime.minusDays(7);
		}
	},
	WEEK_MONDAY(7 * 86_400_000) {
		@Override
		public ZonedDateTime getPartition(ZonedDateTime zonedDateTime) {
			zonedDateTime = zonedDateTime.truncatedTo(ChronoUnit.DAYS);
			zonedDateTime = zonedDateTime.minusDays(zonedDateTime.getDayOfWeek().ordinal());
			return zonedDateTime;
		}

		@Override
		public ZonedDateTime increment(ZonedDateTime zonedDateTime) {
			return zonedDateTime.plusDays(7);
		}

		@Override
		public ZonedDateTime decrement(ZonedDateTime zonedDateTime) {
			return zonedDateTime.minusDays(7);
		}
	},
	MONTH(2_592_000_000L) {
		@Override
		public ZonedDateTime getPartition(ZonedDateTime zonedDateTime) {
			return zonedDateTime.truncatedTo(ChronoUnit.DAYS).withDayOfMonth(1);
		}

		@Override
		public ZonedDateTime increment(ZonedDateTime zonedDateTime) {
			return zonedDateTime.plusMonths(1);
		}

		@Override
		public ZonedDateTime decrement(ZonedDateTime zonedDateTime) {
			return zonedDateTime.minusMonths(1);
		}
	},
	QUARTER(3 * 2_592_000_000L) {
		@Override
		public ZonedDateTime getPartition(ZonedDateTime zonedDateTime) {
			zonedDateTime = zonedDateTime.truncatedTo(ChronoUnit.DAYS).withDayOfMonth(1);
			return zonedDateTime.withMonth(3 * ((zonedDateTime.getMonthValue() - 1) / 3) + 1);
		}

		@Override
		public ZonedDateTime increment(ZonedDateTime zonedDateTime) {
			return zonedDateTime.plusMonths(3);
		}

		@Override
		public ZonedDateTime decrement(ZonedDateTime zonedDateTime) {
			return zonedDateTime.minusMonths(3);
		}
	},
	HALF_YEAR(6 * 2_592_000_000L) {
		@Override
		public ZonedDateTime getPartition(ZonedDateTime zonedDateTime) {
			zonedDateTime = zonedDateTime.truncatedTo(ChronoUnit.DAYS).withDayOfMonth(1);
			return zonedDateTime.withMonth(6 * ((zonedDateTime.getMonthValue() - 1) / 6) + 1);
		}

		@Override
		public ZonedDateTime increment(ZonedDateTime zonedDateTime) {
			return zonedDateTime.plusMonths(6);
		}

		@Override
		public ZonedDateTime decrement(ZonedDateTime zonedDateTime) {
			return zonedDateTime.minusMonths(6);
		}
	},
	YEAR(12 * 2_592_000_000L) {
		@Override
		public ZonedDateTime getPartition(ZonedDateTime zonedDateTime) {
			return ZonedDateTime.of(zonedDateTime.getYear(), 1, 1, 0, 0, 0, 0, zonedDateTime.getZone());
		}

		@Override
		public ZonedDateTime increment(ZonedDateTime zonedDateTime) {
			return zonedDateTime.plusYears(1);
		}

		@Override
		public ZonedDateTime decrement(ZonedDateTime zonedDateTime) {
			return zonedDateTime.minusYears(1);
		}
	};

	private final long averageMilliseconds;

	TimePartitionUnit(long averageMilliseconds) {
		this.averageMilliseconds = averageMilliseconds;
	}

	public long getAverageMilliseconds() {
		return averageMilliseconds;
	}

	public abstract ZonedDateTime getPartition(ZonedDateTime zonedDateTime);

	public abstract ZonedDateTime increment(ZonedDateTime zonedDateTime);

	public abstract ZonedDateTime decrement(ZonedDateTime zonedDateTime);

	@Override
	public long getApproximateMillisecondsPerDataPoint() {
		return averageMilliseconds;
	}
}
