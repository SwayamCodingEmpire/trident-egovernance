package com.trident.egovernance.dto;

import java.util.List;

public record Message(
        String subject,
        Body body,
        List<Recipient> toRecipients,
        List<Attachment> attachments,
        String importance
//        List<Recipient> replyTo
) {
}
