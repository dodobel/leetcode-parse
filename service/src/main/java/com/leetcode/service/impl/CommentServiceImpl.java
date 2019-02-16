package com.leetcode.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.leetcode.model.comment.CommentReqBody;
import com.leetcode.model.comment.CommentStatus;
import com.leetcode.model.response.APIResponse;
import com.leetcode.service.CommentService;
import org.apache.http.HttpEntity;
import org.apache.http.client.CookieStore;
import org.apache.http.entity.StringEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import static com.leetcode.util.HttpUtil.getErrorIfFailed;
import static com.leetcode.util.HttpUtil.post;
import static com.leetcode.util.RequestParamUtil.buildCommentReqBody;


@Service
public class CommentServiceImpl implements CommentService {
    private static final Logger LOGGER = LoggerFactory.getLogger(CommentServiceImpl.class);

    private static final String CREATE_COMMENT_OPERATION = "createComment";
    private static final String UPDATE_COMMENT_OPERATION = "updateComment";
    private static final String DELETE_COMMENT_OPERATION = "deleteComment";

    @Override
    public APIResponse createComment(CommentReqBody req) {
        APIResponse error = checkParams(req, CREATE_COMMENT_OPERATION);
        if (error != null) return error;
        StringEntity requestBody = buildCommentReqBody(req.getTopicId(), req.getParentCommentId(), req.getContent());
        return comment(req, requestBody, CREATE_COMMENT_OPERATION);
    }

    @Override
    public APIResponse updateComment(CommentReqBody req) {
        APIResponse error = checkParams(req, UPDATE_COMMENT_OPERATION);
        if (error != null) return error;
        StringEntity requestBody = buildCommentReqBody(req.getCommentId(), req.getContent());
        return comment(req, requestBody, UPDATE_COMMENT_OPERATION);
    }

    @Override
    public APIResponse deleteComment(CommentReqBody req) {
        APIResponse error = checkParams(req, DELETE_COMMENT_OPERATION);
        if (error != null) return error;
        StringEntity requestBody = buildCommentReqBody(req.getCommentId());
        return comment(req, requestBody, DELETE_COMMENT_OPERATION);
    }

    private APIResponse comment(CommentReqBody req, HttpEntity entity, String operationName) {
        CookieStore cookieStore = req.getCookieStore();
        String res = post(req.getUri(), cookieStore, entity);
        APIResponse e = getErrorIfFailed(res);
        if (e != null) return e;
        JSONObject data = JSONObject.parseObject(res).getJSONObject("data");
        data = data.getJSONObject(operationName);
        CommentStatus status = JSON.parseObject(data.toString(), CommentStatus.class);
        return new APIResponse(status);
    }

    private APIResponse checkParams(CommentReqBody req, String operationName) {
        APIResponse error = checkBasicParams(req, operationName);
        if (error != null) return error;
        if (CREATE_COMMENT_OPERATION.equals(operationName) && req.getTopicId() == null) {
            return new APIResponse(400, "Topic id is required.");
        }
        if ((UPDATE_COMMENT_OPERATION.equals(operationName)
                || DELETE_COMMENT_OPERATION.equals(operationName))
                && req.getCommentId() == null) {
            return new APIResponse(400, "Comment id is required.");
        }
        if (StringUtils.isEmpty(req.getContent()) && !DELETE_COMMENT_OPERATION.equals(operationName)) {
            return new APIResponse(400, "Comment content is required.");
        }
        return null;
    }

    private APIResponse checkBasicParams(CommentReqBody req, String operationName) {
        if (StringUtils.isEmpty(req.getUri())) {
            return new APIResponse(400, "Refer uri is required.");
        }
        if (req.getOperationName() == null || !req.getOperationName().equals(operationName)) {
            return new APIResponse(400, "Operation name is incorrect.");
        }
        if (CollectionUtils.isEmpty(req.getCookies())) {
            return new APIResponse(400, "User cookie is required.");
        }
        return null;
    }
}