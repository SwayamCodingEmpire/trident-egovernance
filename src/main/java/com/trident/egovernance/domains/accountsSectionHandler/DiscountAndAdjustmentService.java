package com.trident.egovernance.domains.accountsSectionHandler;

import com.trident.egovernance.global.entities.permanentDB.Adjustments;
import com.trident.egovernance.global.entities.permanentDB.Discount;

public interface DiscountAndAdjustmentService {
    Boolean insertDiscountData(Discount discount);
    Adjustments addAdjustment(Adjustments adjustments);
}
