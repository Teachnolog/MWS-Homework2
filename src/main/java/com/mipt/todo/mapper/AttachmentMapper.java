package com.mipt.todo.mapper;

import com.mipt.todo.dto.AttachmentResponseDto;
import com.mipt.todo.model.TaskAttachment;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AttachmentMapper {
  AttachmentResponseDto toResponseDto(TaskAttachment attachment);
}

