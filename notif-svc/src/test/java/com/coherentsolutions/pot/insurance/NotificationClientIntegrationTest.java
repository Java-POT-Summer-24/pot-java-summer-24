package com.coherentsolutions.pot.insurance;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.coherentsolutions.pot.insurance.controller.NotificationController;
import com.coherentsolutions.pot.insurance.entity.NotificationEntity;
import com.coherentsolutions.pot.insurance.service.NotificationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(NotificationController.class)
@TestPropertySource(properties = {
		"spring.mail.username=test@example.com"
})
public class NotificationClientIntegrationTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockBean
	private NotificationService notificationService;

	@Test
	void testSendMail() throws Exception {
		String mail = "recipient@example.com";
		NotificationEntity notificationEntity = new NotificationEntity();
		notificationEntity.setSubject("Test Subject");
		notificationEntity.setMessage("Test Message");

		Mockito.doNothing().when(notificationService).sendMail(eq(mail), any(NotificationEntity.class));

		String notificationJson = objectMapper.writeValueAsString(notificationEntity);

		mockMvc.perform(post("/v1/notification/send/{mail}", mail)
						.contentType(MediaType.APPLICATION_JSON)
						.content(notificationJson))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$").value("Mail sent successfully"))
				.andDo(print());
	}
}
