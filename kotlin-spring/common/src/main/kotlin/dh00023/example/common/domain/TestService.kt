package dh00023.example.common.domain

import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class TestService(val repository: TestRepository) {
    fun getOne(id: Long): Test = repository.findByIdOrNull(id) ?: throw Exception()
}
