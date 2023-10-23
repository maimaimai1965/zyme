package ua.mai.zyme.rest.zoo.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ua.mai.zyme.rest.zoo.models.Animal;
import ua.mai.zyme.rest.zoo.services.ZooService;

import java.util.Arrays;
import java.util.List;

@Slf4j
@RestController()
@RequestMapping("${zyme.rest-services.zoo.api-url}")
public class ZooController {

    ZooService zooService;


    private List<String> roles;

    public ZooController(@Autowired ZooService zooService
//                         @Value("${zyme.rest-services.zoo.roles}") String roles
    ) {
        this.zooService = zooService;
//        this.roles = (roles == null || roles.isEmpty()) ? null : Arrays.stream(roles.split(",")).toList();
    }

    @GetMapping(value = "/animals", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Animal[]> getAnimals() {
        List<Animal> animals = zooService.getAnimals();
        return ResponseEntity.ok(animals.toArray(Animal[]::new));
    }

//    //  URL: /client/{clientId}/transactions?from={FromDate}&to={toDate}
//    @GetMapping(value = "/{clientId}/transactions", consumes = MediaType.APPLICATION_JSON_VALUE)
//    public ResponseEntity<SubscriberTransactionsResponse> getSubscriberTransactions(
//          @NotNull @PathVariable(required = true) @Length(max=16) String clientId,
//          @NotNull @RequestParam(required = true) String from,
//          @NotNull @RequestParam(required = true) String to,
//          @RequestParam(required = false) List<String> service,
//          @RequestParam(required = false) List<String> state,
//          @RequestParam(value = "final", required = false) String onlyFinalState,
//          @RequestParam(required = false) List<String> partner
//    ) {
//        checkAuthority();
//        try {
//            checkStateAndFinal(state, onlyFinalState);
//            LocalDateTime fromDate = toLocalDateTime("from", from);
//            LocalDateTime toDate = toLocalDateTime("to", to).plusDays(1).minusSeconds(1);
//            List<Integer> serviceList = toIntegerList("service", service);
//            List<Integer> partnerList = toIntegerList("partner", partner);
//            SubscriberTransaction[] ar;
//
//            String msdisdnOrBan = clientId;
//            SubsIdentType subsIdentType = SubsIdentType.ACCOUNT_NO;
//            if (clientId.length() == 12 && clientId.startsWith("380")) {
//                msdisdnOrBan = clientId.substring(3);
//                subsIdentType = SubsIdentType.MSISDN;
//            }
//            ar = transactionHistoryService.getSubscriberTransactions(msdisdnOrBan, subsIdentType,
//                    fromDate, toDate, serviceList, state, toFinalState("final", onlyFinalState), partnerList);
//            SubscriberTransactionsResponse response= SubscriberTransactionsResponse.builder()
//                  .clientId(clientId)
//                  .data(ar)
//                  .build();
//            return ResponseEntity.ok(response);
//        } catch (RestFaultException ex) {
//            throw ex;
//        } catch (NumberFormatException ex) {
//            throw new RestFaultException(RestFaults.UNSUPPORTED_PARAMETER_VALUE, ex.toString(), ex, "clientId", clientId);
//        } catch (Exception ex) {
//            throw new RestFaultException(RestFaults.OTHERS_ERROR, ex.getMessage(), ex);
//        }
//    }

//    @RequestMapping(value = "**", consumes = MediaType.APPLICATION_JSON_VALUE)
//    public ResponseEntity<SubscriberTransactionsResponse> getSubscriberTransactions() {
//        throw new RestFaultException(RestFaults.INVALID_REQUEST, null);
//    }
//
//    private void checkAuthority() {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        if (authentication != null && roles != null && !authentication.getAuthorities().stream().anyMatch(auth -> roles.contains(auth.getAuthority()))) {
//            throw new RestFaultException(RestFaults.NO_PERMISION, null);
//        }
//    }

}

