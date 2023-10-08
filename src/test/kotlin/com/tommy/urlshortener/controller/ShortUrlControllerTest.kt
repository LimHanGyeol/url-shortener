package com.tommy.urlshortener.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.ninjasquad.springmockk.MockkBean
import com.tommy.urlshortener.dto.ShortUrlRequest
import com.tommy.urlshortener.dto.ShortUrlResponse
import com.tommy.urlshortener.service.UrlShortService
import com.tommy.urlshortener.service.UrlValidator
import io.mockk.every
import io.mockk.justRun
import io.mockk.verify
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
    @MockkBean private val urlValidator: UrlValidator,
    @MockkBean private val urlShortService: UrlShortService,
) {

    @Test
    @DisplayName("입력받은 원본 URL을 단축하여 ShortUrl을 리턴한다.")
    fun shortUrl() {
        // Arrange
        val originUrl = "https://github.com/LimHanGyeol/url-shortener/blob/master/src/main/kotlin/com/tommy/urlshortener/UrlShortenerApplication.kt"
        val shortUrlRequest = ShortUrlRequest(originUrl)
        val shortUrlResponse = ShortUrlResponse("EysI9lHD")

        justRun { urlValidator.validate(shortUrlRequest.originUrl) }
        every { urlShortService.shorten(shortUrlRequest) } returns shortUrlResponse

        // Act & Assert
        mockMvc.perform(
            post("/shorten").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(shortUrlRequest))
        )
            .andDo(print())
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.shortUrl").value(shortUrlResponse.shortUrl))

        verify {
            urlShortService.shorten(shortUrlRequest)
        }
    }
}
