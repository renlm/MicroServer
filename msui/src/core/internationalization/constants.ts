import { ResourceKey } from "i18next";
import { uniq } from "lodash";

export const ENGLISH_US = "en-US";
export const CHINESE_SIMPLIFIED = "zh-CN";

export const DEFAULT_LANGUAGE = ENGLISH_US;

export type LocaleFileLoader = () => Promise<ResourceKey>;

export interface LanguageDefinition<Namespace extends string = string> {
  code: string;
  name: string;
  loader: Record<Namespace, LocaleFileLoader>;
}

export const LANGUAGES: LanguageDefinition[] = [
  {
    code: ENGLISH_US,
    name: "English",
    loader: {
      msui: () => import("../../../public/locales/en-US/msui.json"),
    },
  },
  {
    code: CHINESE_SIMPLIFIED,
    name: "中文（简体）",
    loader: {
      msui: () => import("../../../public/locales/zh-CN/msui.json"),
    },
  },
] satisfies Array<LanguageDefinition<"msui">>;

export const VALID_LANGUAGES = LANGUAGES.map((v) => v.code);

export const NAMESPACES = uniq(LANGUAGES.flatMap((v) => Object.keys(v.loader)));
