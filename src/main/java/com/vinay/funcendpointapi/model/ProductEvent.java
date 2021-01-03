package com.vinay.funcendpointapi.model;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class ProductEvent {
    private Long eventId;

    private String eventType;
}
