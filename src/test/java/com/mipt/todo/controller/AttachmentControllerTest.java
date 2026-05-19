package com.mipt.todo.controller;

import com.mipt.todo.service.AttachmentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Тесты для AttachmentController.
 */
@WebMvcTest(AttachmentController.class)
@AutoConfigureMockMvc(addFilters = false)
@TestPropertySource(properties = "app.security.enabled=false")
class AttachmentControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private AttachmentService attachmentService;

  @Test
  void uploadAttachment_withValidFile_returns201() throws Exception {
    MockMultipartFile file = new MockMultipartFile(
        "file",
        "test.txt",
        MediaType.TEXT_PLAIN_VALUE,
        "test content".getBytes()
    );

    mockMvc.perform(multipart("/api/tasks/1/attachments")
        .file(file))
        .andExpect(status().isCreated());
  }

  @Test
  void downloadAttachment_withInvalidId_returns404() throws Exception {
    mockMvc.perform(get("/api/attachments/999999"))
        .andExpect(status().isNotFound());
  }

  @Test
  void deleteAttachment_withValidId_returns204() throws Exception {
    mockMvc.perform(delete("/api/attachments/1"))
        .andExpect(status().isNoContent());
  }

  @Test
  void getTaskAttachments_returns200() throws Exception {
    mockMvc.perform(get("/api/tasks/1/attachments"))
        .andExpect(status().isOk());
  }
}

