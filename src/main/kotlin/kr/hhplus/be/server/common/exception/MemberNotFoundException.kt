package kr.hhplus.be.server.common.exception

class MemberNotFoundException(): CustomException(ErrorCode.MEMBER_NOT_FOUND) {
}