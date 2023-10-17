package com.tommy.urlshortener.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.ninjasquad.springmockk.MockkBean
import com.tommy.urlshortener.dto.OriginUrlResponse
import com.tommy.urlshortener.dto.ShortUrlRequest
import com.tommy.urlshortener.dto.ShortUrlResponse
import com.tommy.urlshortener.exception.BadRequestException
import com.tommy.urlshortener.service.UrlRedirectService
import com.tommy.urlshortener.service.UrlShortService
import com.tommy.urlshortener.service.UrlValidator
import io.mockk.every
import io.mockk.verify
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.header
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@ActiveProfiles("test")
@WebMvcTest(ShortUrlController::class)
class ShortUrlControllerTest @Autowired constructor(
    private val mockMvc: MockMvc,
    private val objectMapper: ObjectMapper,
    @MockkBean private val urlValidator: UrlValidator,
    @MockkBean private val urlShortService: UrlShortService,
    @MockkBean private val urlRedirectService: UrlRedirectService,
) {

    @Test
    @DisplayName("입력받은 원본 URL을 단축하여 ShortUrl을 리턴한다.")
    fun `url shorten`() {
        // Arrange
        val originUrl = "https://github.com/LimHanGyeol/url-shortener/blob/master/src/main/kotlin/com/tommy/urlshortener/UrlShortenerApplication.kt"
        val shortUrlRequest = ShortUrlRequest(originUrl)
        val shortUrlResponse = ShortUrlResponse("EysI9lHD")

        every { urlValidator.validate(shortUrlRequest.originUrl) } returns true
        every { urlShortService.shorten(shortUrlRequest) } returns shortUrlResponse

        // Act & Assert
        mockMvc.perform(
            post("/shorten")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(shortUrlRequest))
        )
            .andDo(print())
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.shortUrl").value(shortUrlResponse.shortUrl))

        verify {
            urlShortService.shorten(shortUrlRequest)
        }
    }

    @Test
    @DisplayName("입력받은 원본 URL이 유효하지 않은 경우 BadRequetException이 발생한다.")
    fun `url shorten failed`() {
        // Arrange
        val originUrl = "url-shortener, limhangyeol"
        val shortUrlRequest = ShortUrlRequest(originUrl)

        val invalidOriginUrlPair = "originUrl.invalid" to "invalid origin url: {0}"

        every { urlValidator.validate(originUrl) } throws BadRequestException(invalidOriginUrlPair, originUrl)

        // Act & Assert
        mockMvc.perform(
            post("/shorten")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(shortUrlRequest))
        )
            .andDo(print())
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.code").value("originUrl.invalid"))
            .andExpect(jsonPath("$.message").value("invalid origin url: url-shortener, limhangyeol"))
            .andExpect(jsonPath("$.errorFields").isEmpty)

        verify { urlValidator.validate(originUrl) }
        verify(exactly = 0) { urlShortService.shorten(shortUrlRequest) }
    }

    @Test
    @DisplayName("단축 URL을 입력 받을 경우 원본 URL로 Redirect 한다.")
    fun redirect() {
        // Arrange
        val shortUrl = "EysI9lHD"
        val originUrl = "https://github.com/LimHanGyeol/url-shortener/blob/master/src/main/kotlin/com/tommy/urlshortener/UrlShortenerApplication.kt"
        val originUrlResponse = OriginUrlResponse(originUrl)

        every { urlRedirectService.findOriginUrl(shortUrl) } returns originUrlResponse

        // Act & Assert
        mockMvc.perform(
            get("/redirect/{shortUrl}", shortUrl)
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andDo(print())
            .andExpect(status().isMovedPermanently)
            .andExpect(header().string(HttpHeaders.LOCATION, originUrl))

        verify {
            urlRedirectService.findOriginUrl(shortUrl)
        }
    }
}
