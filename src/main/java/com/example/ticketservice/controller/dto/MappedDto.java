package com.example.ticketservice.controller.dto;

import java.util.function.Function;

import org.modelmapper.ModelMapper;

public interface MappedDto<EntityType, DtoType extends MappedDto<?, ?>> {

	public Long getId();

	public default EntityType convertToEntity(Function<Long, EntityType> entityFindById, ModelMapper mapper) {
		return convertToEntity(entityFindById, mapper, true);
	}
	
	public default EntityType convertToEntity(Function<Long, EntityType> entityFindById, ModelMapper mapper, boolean readonly) {
		EntityType entity = entityFindById.apply(this.getId());
		if (! readonly) {
			mapper.map(mapper, entity);
		}
		return entity;
	}

	public static <EntityType, DtoType> DtoType convertToDto(EntityType entity, Class<DtoType> dtoClass, ModelMapper mapper) {
	    return mapper.map(entity, dtoClass);
	}
	
}
