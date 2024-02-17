package org.dbs.quiz.repo.sql

import org.dbs.service.consts.R2dbcConsts.SqlConsts.SQL_COUNT_ALL
import org.dbs.service.consts.R2dbcConsts.SqlConsts.SQL_PAGEABLE_CLAUSE


const val SELECT_TEMPLATE_HIST_LIST_FIELDS =
    " SELECT t.template_code, t.name, t.quiz_timeout, t.group_id, g.group_name, t.actual_date "

const val SELECT_TEMPLATE_HIST_LIST =
    """
    FROM quiz_templates_hist t,
         quiz_groups_ref g
    WHERE t.group_id = g.group_id
      AND CASE WHEN :TC IS NOT NULL THEN LOWER(t.template_code) LIKE :TC ELSE TRUE END
      AND CASE WHEN :TN IS NOT NULL THEN LOWER(t.name) LIKE :TN ELSE TRUE END          
        """

const val SELECT_COUNT_TEMPLATE_HIST_LIST =
    """
       $SQL_COUNT_ALL
       $SELECT_TEMPLATE_HIST_LIST           
    """

const val SELECT_PAGEABLE_TEMPLATE_HIST_LIST =
    """
       $SELECT_TEMPLATE_HIST_LIST_FIELDS
       $SELECT_TEMPLATE_HIST_LIST
       ORDER BY t.actual_date DESC
       $SQL_PAGEABLE_CLAUSE
    """
