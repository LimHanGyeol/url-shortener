package com.tommy.urlshortener.repository

import com.querydsl.jpa.impl.JPAQueryFactory
import com.tommy.urlshortener.domain.QShortenUrl.shortenUrl
import com.tommy.urlshortener.domain.ShortenUrl
import org.springframework.data.jpa.repository.JpaRepository
import jakarta.persistence.EntityManager

interface ShortenUrlRepository : JpaRepository<ShortenUrl, Long>, ShortenUrlRepositoryCustom

interface ShortenUrlRepositoryCustom {
    fun findByOriginUrl(originUrl: String): ShortenUrl?
    fun findByShortUrl(shortUrl: String): ShortenUrl?
}

class ShortenUrlRepositoryImpl(
    em: EntityManager,
) : ShortenUrlRepositoryCustom {
    private val queryFactory = JPAQueryFactory(em)

    override fun findByOriginUrl(originUrl: String): ShortenUrl? {
        return queryFactory
            .selectFrom(shortenUrl)
            .where(shortenUrl.originUrl.eq(originUrl))
            .fetchOne()
    }

    override fun findByShortUrl(shortUrl: String): ShortenUrl? {
        return queryFactory
            .selectFrom(shortenUrl)
            .where(shortenUrl.shortUrl.eq(shortUrl))
            .fetchOne()
    }
}
