package com.example.urlshortener;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
class UrlshortenerApplicationTests {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private StringRedisTemplate redisTemplate;

	@Test
	void contextLoads() {
	}

	@Test
	void testShortenAndRetrieveUrl() throws Exception {
		// Mock Redis behavior
		when(redisTemplate.opsForValue().get(anyString())).thenReturn(null);
		when(redisTemplate.opsForValue()).thenReturn(Mockito.mock(StringRedisTemplate.opsForValue().getClass()));

		// POST /shorten
		MvcResult result = mockMvc.perform(post("/shorten")
						.contentType("application/json")
						.content("\"https://example.com\""))  // Note: plain string
				.andExpect(status().isOk())
				.andReturn();

		String shortCode = result.getResponse().getContentAsString().replace("\"", "");

		// GET /{shortCode}
		mockMvc.perform(get("/" + shortCode))
				.andExpect(status().isOk())
				.andExpect(content().string("https://example.com"));
	}

}
