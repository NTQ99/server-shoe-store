package shoe.store.server.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import shoe.store.server.exceptions.GlobalException;
import shoe.store.server.models.DeliveryUnit;
import shoe.store.server.payload.BasePageResponse;
import shoe.store.server.payload.ErrorMessage;
import shoe.store.server.security.jwt.JwtUtils;
import shoe.store.server.services.DeliveryUnitService;

@RestController
@RequestMapping("/api/delivery")
public class DeliveryController {

    @Autowired
    private DeliveryUnitService service;

    @Autowired
	private JwtUtils jwtUtils;

    @PostMapping("/get")
    public ResponseEntity<?> getAll(@RequestHeader("Authorization") String jwt) {

        String userId = jwtUtils.getIdFromJwtToken(jwt.substring(7, jwt.length()));

        List<DeliveryUnit> deliveries = service.getAllDeliveryUnits(userId);

        return new ResponseEntity<>(new BasePageResponse<>(deliveries, ErrorMessage.StatusCode.OK.message), HttpStatus.OK);

    }

    @PostMapping("/get/{id}")
    public ResponseEntity<?> get(@RequestHeader("Authorization") String jwt, @PathVariable("id") String id) {

        String userId = jwtUtils.getIdFromJwtToken(jwt.substring(7, jwt.length()));

        DeliveryUnit deliveryUnit = service.getDeliveryUnitById(id);

        if (!deliveryUnit.validateUser(userId)) throw new GlobalException(ErrorMessage.StatusCode.UNAUTHORIZED.message);

        return new ResponseEntity<>(new BasePageResponse<>(deliveryUnit, ErrorMessage.StatusCode.OK.message), HttpStatus.OK);

    }

    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestHeader("Authorization") String jwt, @RequestBody DeliveryUnit deliveryUnitData) {

        String userId = jwtUtils.getIdFromJwtToken(jwt.substring(7, jwt.length()));
        
        DeliveryUnit currDeliveryData = service.getDeliveryUnitByName(userId, deliveryUnitData.getDeliveryUnitName());

        if (currDeliveryData != null) {
            throw new GlobalException(ErrorMessage.StatusCode.EXIST.message);
        }

        deliveryUnitData.setUserId(userId);
        BasePageResponse<DeliveryUnit> response = new BasePageResponse<>(service.createDeliveryUnit(deliveryUnitData), ErrorMessage.StatusCode.CREATED.message);
        return new ResponseEntity<>(response, HttpStatus.OK);

    }

    @PostMapping("/update/{id}")
    public ResponseEntity<?> update(@RequestHeader("Authorization") String jwt, @PathVariable("id") String id,
            @RequestBody DeliveryUnit newDeliveryData) {

        String userId = jwtUtils.getIdFromJwtToken(jwt.substring(7, jwt.length()));

        DeliveryUnit currDeliveryData = service.getDeliveryUnitById(id);

        if (currDeliveryData == null) {
            throw new GlobalException(ErrorMessage.StatusCode.NOT_FOUND.message);
        }
        
        if (!currDeliveryData.validateUser(userId)) {
            throw new GlobalException(ErrorMessage.StatusCode.UNAUTHORIZED.message);
        }

        BasePageResponse<DeliveryUnit> response = new BasePageResponse<>(service.updateDeliveryUnit(id, newDeliveryData), ErrorMessage.StatusCode.MODIFIED.message);
        return new ResponseEntity<>(response, HttpStatus.OK);

    }

    @PostMapping("/delete/{id}")
    public ResponseEntity<?> delete(@RequestHeader("Authorization") String jwt, @PathVariable("id") String id) {

        String userId = jwtUtils.getIdFromJwtToken(jwt.substring(7, jwt.length()));

        DeliveryUnit currDeliveryData = service.getDeliveryUnitById(id);

        if (currDeliveryData == null) {
            throw new GlobalException(ErrorMessage.StatusCode.NOT_FOUND.message);
        }
        
        if (!currDeliveryData.validateUser(userId)) {
            throw new GlobalException(ErrorMessage.StatusCode.UNAUTHORIZED.message);
        }

        service.deleteDeliveryUnit(id);
        BasePageResponse<DeliveryUnit> response = new BasePageResponse<>(null, ErrorMessage.StatusCode.OK.message);
        return new ResponseEntity<>(response, HttpStatus.OK);

    }

    @PostMapping("/delete")
    public ResponseEntity<?> deleteAll(@RequestHeader("Authorization") String jwt) {

        String userId = jwtUtils.getIdFromJwtToken(jwt.substring(7, jwt.length()));

        service.deleteAllDeliveryUnits(userId);
        BasePageResponse<DeliveryUnit> response = new BasePageResponse<>(null, ErrorMessage.StatusCode.OK.message);
        return new ResponseEntity<>(response, HttpStatus.OK);

    }

}