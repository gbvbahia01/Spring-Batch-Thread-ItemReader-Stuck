package br.com.gbvbahia.threads.monitor.h2;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import javax.sql.DataSource;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Repository
@RequiredArgsConstructor
@Slf4j
public class H2CleanerRepository {

  private static final String SELECT_JOB_INSTANCE_ID =
      "SELECT JOB_INSTANCE_ID FROM BATCH_JOB_EXECUTION WHERE STATUS = 'COMPLETED'";
  private static final String SELECT_JOB_EXECUTION_ID =
      "SELECT JOB_EXECUTION_ID FROM BATCH_JOB_EXECUTION WHERE JOB_INSTANCE_ID IN (:selectJobInstanceCompleted)";
  private static final String SELECT_STEP_EXECUTION_ID =
      "SELECT STEP_EXECUTION_ID FROM BATCH_STEP_EXECUTION WHERE JOB_EXECUTION_ID IN (:selectJobExecutionIdFromCompleted)";

  private static final String DELETE_BATCH_STEP_EXECUTION_CONTEXT =
      "DELETE FROM BATCH_STEP_EXECUTION_CONTEXT WHERE STEP_EXECUTION_ID IN ( :stepIds )";
  private static final String DELETE_BATCH_STEP_EXECUTION =
      "DELETE FROM BATCH_STEP_EXECUTION WHERE STEP_EXECUTION_ID IN ( :stepIds )";
  private static final String DELETE_BATCH_JOB_EXECUTION_CONTEXT =
      "DELETE FROM BATCH_JOB_EXECUTION_CONTEXT WHERE JOB_EXECUTION_ID IN ( :jobExecIds )";
  private static final String DELETE_BATCH_JOB_EXECUTION_PARAMS =
      "DELETE FROM BATCH_JOB_EXECUTION_PARAMS WHERE JOB_EXECUTION_ID IN ( :jobExecIds )";
  private static final String DELETE_BATCH_JOB_EXECUTION =
      "DELETE FROM BATCH_JOB_EXECUTION where JOB_EXECUTION_ID IN  ( :jobExecIds )";
  private static final String DELETE_BATCH_JOB_INSTANCE =
      "DELETE FROM BATCH_JOB_INSTANCE WHERE JOB_INSTANCE_ID IN ( :jobInstanceIds )";

  private final DataSource dataSource;


  public void cleanMemoryCompletedJobs() {
    try {

      NamedParameterJdbcTemplate template = new NamedParameterJdbcTemplate(dataSource);

      List<Long> jobInstanceCompletedIds = findAllIDsJobCompleted(template);

      if (CollectionUtils.isEmpty(jobInstanceCompletedIds)) {
        return;
      }

      List<Long> jobJobExecutionIdFromCompletedIds =
          findAllIDsJobExecutionCompleted(template, jobInstanceCompletedIds);

      if (CollectionUtils.isEmpty(jobJobExecutionIdFromCompletedIds)) {
        return;
      }

      List<Long> stepExecutionIdFromJobExecutionId =
          findAllIDsStepExecutionCompleted(template, jobJobExecutionIdFromCompletedIds);

      if (CollectionUtils.isEmpty(stepExecutionIdFromJobExecutionId)) {
        return;
      }

      deleteByIDsCompleted(template, jobInstanceCompletedIds, jobJobExecutionIdFromCompletedIds,
          stepExecutionIdFromJobExecutionId);

    } catch (Exception e) {
      log.error(String.format("Error on clean H2 Memory: %s", e.getMessage()), e);
    }
  }

  private List<Long> findAllIDsJobCompleted(NamedParameterJdbcTemplate template) {
    
    List<Long> jobInstanceCompletedIds =
        template.query(SELECT_JOB_INSTANCE_ID, (rs, rowNum) -> {
            return rs.getLong(1);
        });
    
    return jobInstanceCompletedIds;
  }
  
  private List<Long> findAllIDsJobExecutionCompleted(NamedParameterJdbcTemplate template,
      List<Long> jobInstanceCompletedIds) {
    
    MapSqlParameterSource jobInstanceCompletedIdsParam = new MapSqlParameterSource();
    jobInstanceCompletedIdsParam.addValue("selectJobInstanceCompleted", jobInstanceCompletedIds);

    List<Long> jobJobExecutionIdFromCompletedIds = template.query(SELECT_JOB_EXECUTION_ID,
        jobInstanceCompletedIdsParam, new RowMapper<Long>() {
          @Override
          public Long mapRow(ResultSet rs, int rowNum) throws SQLException {
            return rs.getLong(1);
          }
        });
    
    return jobJobExecutionIdFromCompletedIds;
  }

  private List<Long> findAllIDsStepExecutionCompleted(NamedParameterJdbcTemplate template,
      List<Long> jobJobExecutionIdFromCompletedIds) {
    
    MapSqlParameterSource jobExecutionIdFromCompletedIdsParam = new MapSqlParameterSource();
    jobExecutionIdFromCompletedIdsParam.addValue("selectJobExecutionIdFromCompleted",
        jobJobExecutionIdFromCompletedIds);

    List<Long> stepExecutionIdFromJobExecutionId = template.query(SELECT_STEP_EXECUTION_ID,
        jobExecutionIdFromCompletedIdsParam, (rs, rowNum) -> {
            return rs.getLong(1);
        });
    
    return stepExecutionIdFromJobExecutionId;
  }

  private void deleteByIDsCompleted(NamedParameterJdbcTemplate template,
      List<Long> jobInstanceCompletedIds, List<Long> jobJobExecutionIdFromCompletedIds,
      List<Long> stepExecutionIdFromJobExecutionId) {
    MapSqlParameterSource stepIdsParam = new MapSqlParameterSource();
    stepIdsParam.addValue("stepIds", stepExecutionIdFromJobExecutionId);

    MapSqlParameterSource jobExecIdsParam = new MapSqlParameterSource();
    jobExecIdsParam.addValue("jobExecIds", jobJobExecutionIdFromCompletedIds);

    MapSqlParameterSource jobInstanceIdsParam = new MapSqlParameterSource();
    jobInstanceIdsParam.addValue("jobInstanceIds", jobInstanceCompletedIds);

    template.update(DELETE_BATCH_STEP_EXECUTION_CONTEXT, stepIdsParam);
    template.update(DELETE_BATCH_STEP_EXECUTION, stepIdsParam);
    template.update(DELETE_BATCH_JOB_EXECUTION_CONTEXT, jobExecIdsParam);
    template.update(DELETE_BATCH_JOB_EXECUTION_PARAMS, jobExecIdsParam);
    template.update(DELETE_BATCH_JOB_EXECUTION, jobExecIdsParam);
    template.update(DELETE_BATCH_JOB_INSTANCE, jobInstanceIdsParam);
  }
  
}
