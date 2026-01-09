package com.me.warepulse.receiving;

import com.me.warepulse.receiving.dto.ReceivingRequest;
import com.me.warepulse.receiving.dto.ReceivingResponse;

public interface ReceivingService {

    ReceivingResponse createReceive(ReceivingRequest request);

    ReceivingResponse inspectedReceive(Long receivingId, String username, int receivedQty);

    ReceivingResponse completedReceive(Long receivingId, String username);
}
