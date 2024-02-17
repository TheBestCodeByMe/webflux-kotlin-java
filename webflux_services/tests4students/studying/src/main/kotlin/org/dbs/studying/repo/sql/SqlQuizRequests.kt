package org.dbs.quiz.repo.sql

const val SELECT_REQUESTS_BY_CUSTOMERS_TEMPLATE =
    """
    SELECT *
    FROM quiz_requests qr
    WHERE qr.cust_id IN (:IDs)
      AND qr.template_id = :TEMP      
    """

const val SELECT_REQUESTS_BY_TEMPLATE_CODE =
    """
    SELECT qr.*
    FROM quiz_requests qr,
         quiz_templates qt
    WHERE qt.template_code = :TEMP
      AND qt.template_id = qr.template_id 
    """

const val SELECT_REQUEST_INFO_4_QUIZ_RESPONSE = """
    SELECT qr.request_id,
           qr.template_id,
           qr.time_spent,
           qr.open_date,
           qr.deadline_date,
           qr.start_date,
           qr.finish_date,
           qr.remain_time,
           qr.cust_id,
           qr.request_code,
           qt.body
    FROM quiz_requests qr,
         quiz_templates qt
    WHERE qr.request_code = :CODE
      AND qt.template_id = qr.template_id
    """
