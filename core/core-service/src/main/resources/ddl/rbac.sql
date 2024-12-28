-- 用户
CREATE TABLE "user"(
    "id" BIGSERIAL PRIMARY KEY,
    "user_id" CHAR(32) NOT NULL,
    "username" VARCHAR(255) NOT NULL,
    "password" VARCHAR(255) NOT NULL,
    "nickname" VARCHAR(255) NOT NULL,
    "realname" VARCHAR(255),
    "birthday" DATE,
    "sex" VARCHAR(1),
    "mobile" VARCHAR(30),
    "email" VARCHAR(128),
    "enabled" BOOLEAN DEFAULT TRUE NOT NULL,
    "account_non_expired" BOOLEAN DEFAULT TRUE NOT NULL,
    "credentials_non_expired" BOOLEAN DEFAULT TRUE NOT NULL,
    "account_non_locked" BOOLEAN DEFAULT TRUE NOT NULL,
    "created_at" TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updated_at" TIMESTAMP,
    "remark" VARCHAR(800)
)WITH(OIDS=FALSE);
COMMENT ON TABLE "user" IS '用户';
COMMENT ON COLUMN "user"."id" IS '主键ID';
COMMENT ON COLUMN "user"."user_id" IS '用户ID';
COMMENT ON COLUMN "user"."username" IS '登录账号';
COMMENT ON COLUMN "user"."password" IS '密码';
COMMENT ON COLUMN "user"."nickname" IS '昵称';
COMMENT ON COLUMN "user"."realname" IS '真实姓名';
COMMENT ON COLUMN "user"."birthday" IS '出生日期';
COMMENT ON COLUMN "user"."sex" IS '性别，M：男，F：女';
COMMENT ON COLUMN "user"."mobile" IS '手机号码';
COMMENT ON COLUMN "user"."email" IS '邮箱地址';
COMMENT ON COLUMN "user"."enabled" IS '是否启用（默认启用）';
COMMENT ON COLUMN "user"."account_non_expired" IS '账户未过期（默认未过期）';
COMMENT ON COLUMN "user"."credentials_non_expired" IS '凭证未过期（默认未过期）';
COMMENT ON COLUMN "user"."account_non_locked" IS '账号未锁定（默认未锁定）';
COMMENT ON COLUMN "user"."created_at" IS '创建时间';
COMMENT ON COLUMN "user"."updated_at" IS '更新时间';
COMMENT ON COLUMN "user"."remark" IS '备注';