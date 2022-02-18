package dead.souls.feolife.controller

import dead.souls.feolife.model.request.CreateRelocationRequestRequest
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("relocation-requests")
class RelocationRequestsController {
    @GetMapping
    fun getRequests() {

    }

    @PostMapping
    fun createRelocationRequest(
        @RequestBody body: CreateRelocationRequestRequest
    ) {

    }
}
