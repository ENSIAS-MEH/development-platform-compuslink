bothipackage com.CompusLink.CompusLink.domain.offer.controller;

import com.CompusLink.CompusLink.domain.offer.dto.ApplicationResponse;
import com.CompusLink.CompusLink.domain.offer.dto.ApplyRequest;
import com.CompusLink.CompusLink.domain.offer.dto.UpdateApplicationStatusRequest;
import com.CompusLink.CompusLink.domain.offer.service.ApplicationService;
import com.CompusLink.CompusLink.domain.user.model.UserPrincipal;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class ApplicationController {

    private final ApplicationService applicationService;

    public ApplicationController(ApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    @PostMapping("/offers/{offerId}/applications")
    public ResponseEntity<ApplicationResponse> apply(@PathVariable UUID offerId,
                                                     @Valid @RequestBody ApplyRequest request,
                                                     @AuthenticationPrincipal UserPrincipal principal) {
        ApplicationResponse response = applicationService.apply(offerId, request, principal.getUser().getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/offers/{offerId}/applications")
    public List<ApplicationResponse> getByOffer(@PathVariable UUID offerId,
                                                @AuthenticationPrincipal UserPrincipal principal) {
        return applicationService.getByOffer(offerId, principal.getUser().getId());
    }

    @GetMapping("/me/applications")
    public List<ApplicationResponse> getMyApplications(@AuthenticationPrincipal UserPrincipal principal) {
        return applicationService.getMyApplications(principal.getUser().getId());
    }

    @PatchMapping("/applications/{id}/status")
    public ApplicationResponse updateStatus(@PathVariable UUID id,
                                            @Valid @RequestBody UpdateApplicationStatusRequest request,
                                            @AuthenticationPrincipal UserPrincipal principal) {
        return applicationService.updateStatus(id, request, principal.getUser().getId());
    }
}
