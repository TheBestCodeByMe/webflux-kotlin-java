package org.dbs.banking.repo.sql

const val SELECT_ACCOUNT_BY_NUM_STATUS_SUM = """
    WITH c_accounts_by_params AS
      (SELECT ca.*,
              rest_date
       FROM b_accounts ca,
            core_entities e,
            b_accounts_rests ar
       WHERE ca.account_id = e.entity_id
         AND ca.account_id = ar.account_id
         AND ca.account = :account
         AND (e.entity_status_id = :entityStatusId
              OR :entityStatusId IS NULL)
         AND (ar.rest_amount >= :sum
              OR :sum IS NULL)
         AND ar.rest_date <= :restDate),
         c_account_with_particion AS
      (SELECT c_abp.*,
              row_number() OVER (PARTITION BY account_id
                                 ORDER BY rest_date DESC) AS rn
       FROM c_accounts_by_params c_abp)
    SELECT account_id,
           customer_id,
           account_name,
           account,
           currency_id
    FROM c_account_with_particion a_awp
    WHERE a_awp.rn = 1;
    """

const val SELECT_ACCOUNT_BY_NUM_STATUS = """
       SELECT ca.*
       FROM b_accounts ca,
            core_entities e
       WHERE ca.account_id = e.entity_id
         AND ca.account = :account
         AND e.entity_status_id = :entityStatusId
    """