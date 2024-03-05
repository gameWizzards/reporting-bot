package com.telegram.reporting.mapper;

import com.telegram.reporting.dto.TimeRecordTO;
import com.telegram.reporting.domain.TimeRecord;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring",
        unmappedSourcePolicy = ReportingPolicy.IGNORE, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TimeRecordMapper {

    @Mapping(target = "hours", source = "minutes", qualifiedByName = "toHours")
    @Mapping(target = "reportId", source = "report.id")
    @Mapping(target = "categoryNameKey", source = "category.nameKey")
    TimeRecordTO toDto(TimeRecord timeRecord);

    @Mapping(target = "minutes", source = "hours", qualifiedByName = "toMinutes")
    TimeRecord toEntity(TimeRecordTO timeRecordTO);

    List<TimeRecordTO> toDtos(List<TimeRecord> timeRecords);

    List<TimeRecord> toEntities(List<TimeRecordTO> timeRecordTOs);

    @Named("toHours")
    default Integer toHours(Integer minutes) {
        return minutes / 60;
    }

    @Named("toMinutes")
    default Integer toMinutes(Integer hours) {
        return hours * 60;
    }
}
