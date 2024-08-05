package org.gsstation.novin.core.dao.domain;

import lombok.Data;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by A_Tofigh at 8/5/2024
 */

@Data
@Entity
@Table(name = "SHIFT_TRANSACTION_RECORD")
public class ShiftTransactionRecord extends BaseEntityTrx {
    @EmbeddedId
    private TransactionKey id;
}
