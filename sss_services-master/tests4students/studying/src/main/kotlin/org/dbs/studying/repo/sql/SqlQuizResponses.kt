package org.dbs.quiz.repo.sql

const val SELECT_QUIZ_RESPONSES_BY_QUIZ_REQUEST_CODE = """
    SELECT qp.response_id,
           qp.request_id,
           qp.result,
           qp.actual_responses,
           qp.question_num,
           qp.valid_responses,
           qp.response_code,
           qp.question_body
    FROM quiz_responses qp,
         quiz_requests qr
    WHERE qr.request_code = :CODE
      AND qp.request_id = qr.request_id
    """
