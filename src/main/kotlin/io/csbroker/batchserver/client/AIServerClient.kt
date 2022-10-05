package io.csbroker.batchserver.client


import io.csbroker.batchserver.dto.GradingRequestDto
import io.csbroker.batchserver.dto.GradingResponseDto
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody

@FeignClient(name = "ai", url = "\${feign.ai.url}")
interface AIServerClient {

    @PostMapping("/integrate_predict")
    fun getGrade(@RequestBody gradingRequestDto: GradingRequestDto): GradingResponseDto
}
