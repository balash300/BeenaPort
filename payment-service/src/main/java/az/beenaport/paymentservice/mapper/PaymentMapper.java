package az.beenaport.paymentservice.mapper;

import az.beenaport.paymentservice.dto.response.PaymentAttemptResponse;
import az.beenaport.paymentservice.dto.response.PaymentResponse;
import az.beenaport.paymentservice.entity.Payment;
import az.beenaport.paymentservice.entity.PaymentAttempt;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PaymentMapper {

    @Mapping(target = "attempts", source = "attempts")
    PaymentResponse toResponse(Payment payment);

    @Mapping(target = "paymentId", source = "payment.id")
    PaymentAttemptResponse toAttemptResponse(PaymentAttempt attempt);
}