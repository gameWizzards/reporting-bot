package com.telegram.reporting.mapper;

import java.util.List;

public interface BaseMapper <E, D> {

    D toDto(E entity);

    E toEntity(D dto);

    List<D> toDtos(List<E> entities);

    List<E> toEntities(List<D> dtos);

}
