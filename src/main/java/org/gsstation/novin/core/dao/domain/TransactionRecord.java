package org.gsstation.novin.core.dao.domain;

import lombok.Data;

import javax.persistence.*;

/**
 * Created by A_Tofigh at 7/25/2024
 */

@Data
@Entity
@Table(name = "TRANSACTION_RECORD")
public class TransactionRecord extends BaseEntityTrx {
    @EmbeddedId
    private TransactionKey id;
}
