package com.jorgereyesdev.spring_security.infrastructure.services

import com.jorgereyesdev.spring_security.domain.models.GrantType
import com.jorgereyesdev.spring_security.domain.models.Token
import com.jorgereyesdev.spring_security.domain.models.User
import com.jorgereyesdev.spring_security.domain.services.TokenService
import com.jorgereyesdev.spring_security.infrastructure.extensions.toEntity
import com.jorgereyesdev.spring_security.infrastructure.extensions.toTDomain
import com.jorgereyesdev.spring_security.infrastructure.repositories.TokenRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class TokenServiceImpl(private val tokenRepository: TokenRepository) : TokenService {
    private val log: Logger = LoggerFactory.getLogger(TokenServiceImpl::class.java)

    @Transactional
    override suspend fun saveToken2(token: Token): Result<Token> {
        return runCatching {
            val tokenEntity = tokenRepository.save(token.toEntity())
            tokenEntity.toTDomain()
        }.onFailure {
            log.error(it.message)
        }
    }

    @Transactional(readOnly = true)
    override fun findByToken(value: String): Token?  {
        val token = tokenRepository.findByToken(value)
        return  token?.toTDomain()
    }

    @Transactional
    override fun saveToken(token: Token): Token {
        val tokenEntity = tokenRepository.save(token.toEntity())
        return tokenEntity.toTDomain()
    }

    @Transactional
    override fun revokeAllUserValidTokens(user: User) {
        val validTokens =
            user.id?.let { tokenRepository.findAllValidByUserId(it) } ?: throw NoSuchElementException("Not found")

        if (validTokens.isNotEmpty()) {
            val tokens = validTokens.map {
                it.revoke().expire()
            }
            tokenRepository.saveAll(tokens)
        }
    }

    @Transactional
    override fun revokeUserTokensByGrantType(user: User, grantType: GrantType) {
        val validTokens =
            user.id?.let { tokenRepository.findAllValidByUserId(it) } ?: throw NoSuchElementException("Not found")

        if (validTokens.isNotEmpty()) {
            val tokens = validTokens.filter { it.grantType == grantType }.map {
                it.revoke().expire()
            }
            tokenRepository.saveAll(tokens)
        }
    }

//    @Transactional
//    override suspend fun revokeAllUserTokens(user: User): Result<Unit> {
//        return runCatching {
//            val validTokens =
//                user.id?.let { tokenRepository.findAllValidByUserId(it) } ?: throw NoSuchElementException("Not found")
//
//            if (validTokens.isNotEmpty()) {
//                val tokens = validTokens.map {
//                    it.revoke().expire()
//                }
//                val invalidTokens = tokenRepository.saveAll(tokens)
//                invalidTokens.toList().isNotEmpty()
//            }
//
//        }.onFailure {
//            log.error(it.message)
//        }
//    }

}