package com.mipt.todo.mapper;

import com.mipt.todo.dto.TaskCreateDto;
import com.mipt.todo.dto.TaskResponseDto;
import com.mipt.todo.dto.TaskUpdateDto;
import com.mipt.todo.model.Task;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

/**
 * Маппер для конвертации между Task и DTO с использованием MapStruct.
 */
@Mapper(componentModel = "spring", uses = {AttachmentMapper.class})
public interface TaskMapper {

  /**
   * Преобразует DTO создания в сущность Task.
   */
  Task toEntity(TaskCreateDto dto);

  /**
   * Частичное обновление существующей сущности на основе DTO.
   * null-значения в DTO не перезаписывают существующие поля сущности.
   */
  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  void updateEntity(TaskUpdateDto dto, @MappingTarget Task task);

  /**
   * Преобразует сущность Task в DTO ответа.
   */
  TaskResponseDto toResponseDto(Task task);
}