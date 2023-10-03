package com.tommy.urlshortener.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.tommy.urlshortener.dto.ShortUrlRequest
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@ActiveProfiles("test")
@WebMvcTest(ShortUrlController::class)
class ShortUrlControllerTest @Autowired constructor(
    private val mockMvc: MockMvc,
    private val objectMapper: ObjectMapper,
) {

    @Test
    @DisplayName("입력받은 원본 URL을 단축하여 ShortUrl을 리턴한다.")
    fun shortUrl() { // Arrange
        val originUrl = "https://github.com/LimHanGyeol/url-shortener"
        val shortUrlRequest = ShortUrlRequest(originUrl)

        // TODO: url service stub

        // Act & Assert
        mockMvc.perform(
            post("/shorten").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(shortUrlRequest))
        )
            .andDo(print())
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.shortUrl").value(""))

        // TODO: url service verify
    }
}
