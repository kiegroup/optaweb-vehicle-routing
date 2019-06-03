/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

interface WindowWithEnv {
  env: {
    REACT_APP_BACKEND_URL: string;
  };
}

const processOrWindowEnv = (key: keyof WindowWithEnv['env']): string | undefined => {
  const env = (window as unknown as WindowWithEnv).env;
  return (env && env[key]) || process.env[key];
};

/**
 * Backend URL.
 */
export const BACKEND_URL = processOrWindowEnv('REACT_APP_BACKEND_URL');
