package com.sahe.itera.domain.usecase.subject

import com.sahe.itera.domain.model.Subject
import com.sahe.itera.domain.repository.SubjectRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetSubjectsUseCase @Inject constructor(
    private val repository: SubjectRepository
) {
    operator fun invoke(): Flow<List<Subject>> = repository.getSubjects()
}