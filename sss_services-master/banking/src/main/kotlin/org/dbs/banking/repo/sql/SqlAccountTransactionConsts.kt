package org.dbs.banking.repo.sql

import org.dbs.consts.RestHttpConsts.RestQueryParams.Pagination.ACC_TRANSACTION_DEFAULT_SORT_FIELD
import org.dbs.service.consts.R2dbcConsts.SqlConsts.SQL_COUNT_ALL
import org.dbs.service.consts.R2dbcConsts.SqlConsts.SQL_PAGEABLE_CLAUSE

const val CALL_PROCEDURE_INTERNAL_TRANSACTION = """
    CALL createaccounttransaction(:REC_ACC_ID, :SUP_ACC_ID, :DATE, :AMOUNT, :REC_TYPE, :SUP_TYPE, :REC_TRANSCODE, :SUP_TRANSCODE, :NOTE, :CURR_ID)
    """

//--------------------------------------------------------LIST----------------------------------------------------------

const val FROM_AND_WHERE_ACCOUNT_QUERY = """
    FROM b_accounts_transactions bat,
         b_accounts a,
         b_transaction_statuses_ref ts,
         b_transaction_kinds_ref tk
    WHERE bat.transaction_status_id = ts.status_id
      AND bat.transaction_type_id = tk.kind_id
      AND bat.account_id = a.account_id
      AND CASE
              WHEN :STATUS IS NOT NULL THEN LOWER(ts.status_name) LIKE :STATUS
              ELSE TRUE
          END
      AND CASE
              WHEN :KIND IS NOT NULL THEN LOWER(tk.kind_name) LIKE :KIND
              ELSE TRUE
          END
      AND CASE
              WHEN :ACCOUNT IS NOT NULL THEN LOWER(a.account) LIKE :ACCOUNT
              ELSE TRUE
          END
      AND bat.transaction_date BETWEEN :DATE_FROM AND :DATE_TO
      AND CASE
              WHEN :SUM IS NOT NULL THEN bat.transaction_sum::tstr1000 LIKE :SUM
              ELSE TRUE
          END
    """

const val ORDER_BY_FIELDS_ACCOUNT_TRANSACTIONS = """
    CASE :sortField
        WHEN '$ACC_TRANSACTION_DEFAULT_SORT_FIELD' THEN bat.transaction_date::varchar
    END
    """

const val ORDER_BY_ACCOUNT_TRANSACTIONS = """
    ORDER BY CASE
                 WHEN upper(:sortOrder) = 'ASC' THEN $ORDER_BY_FIELDS_ACCOUNT_TRANSACTIONS
             END ASC, CASE
                          WHEN upper(:sortOrder) = 'DESC' THEN $ORDER_BY_FIELDS_ACCOUNT_TRANSACTIONS
                      END DESC
    """

const val SELECT_ACCOUNT_FIELDS = """
    SELECT bat.currency_id,
           bat.transaction_date,
           bat.transaction_num,
           bat.transaction_sum,
           a.account,
           bat.transaction_status_id,
           bat.transaction_type_id
           $FROM_AND_WHERE_ACCOUNT_QUERY 
           $ORDER_BY_ACCOUNT_TRANSACTIONS
    $SQL_PAGEABLE_CLAUSE
"""

const val SELECT_COUNT_ACCOUNT_TRANSACTIONS = """
    $SQL_COUNT_ALL $FROM_AND_WHERE_ACCOUNT_QUERY
"""

//----------------------------------------------------------------------------------------------------------------------

const val FIND_ACCOUNT_TRANSACTION_CARD_BY_CODE = """
    WITH c_common_info AS
      (SELECT ba.account,
              ba.account_name,
              bat.transaction_note,
              bat.transaction_num,
              bat.transaction_status_id,
              bat.transaction_type_id,
              bat.transaction_sum,
              bat.currency_id,
              bat.transaction_date,
              bat.linked_transaction_id
       FROM b_accounts ba,
            b_accounts_transactions bat
       WHERE bat.transaction_num = :CODE
         AND ba.account_id = bat.account_id)
    SELECT c_ci.account,
           c_ci.account_name,
           c_ci.transaction_note,
           c_ci.transaction_num,
           c_ci.transaction_status_id,
           c_ci.transaction_type_id,
           c_ci.transaction_sum,
           c_ci.currency_id,
           c_ci.transaction_date,
           ba.account AS account_sec,
           ba.account_name AS account_name_sec
    FROM c_common_info c_ci,
         b_accounts_transactions bac,
         b_accounts ba
    WHERE c_ci.linked_transaction_id = bac.transaction_id
      AND bac.account_id = ba.account_id
    """
