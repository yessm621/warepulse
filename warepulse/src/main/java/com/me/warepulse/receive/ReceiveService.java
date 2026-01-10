package com.me.warepulse.receive;

import com.me.warepulse.receive.dto.ReceiveRequest;
import com.me.warepulse.receive.dto.ReceiveResponse;

import java.util.List;

public interface ReceiveService {

    List<ReceiveResponse> findReceives();

    ReceiveResponse findReceive(Long receiveId);

    ReceiveResponse createReceive(ReceiveRequest request);

    ReceiveResponse inspectedReceive(Long receiveId, String username, int receivedQty);

    ReceiveResponse completedReceive(Long receiveId, String username);
}
