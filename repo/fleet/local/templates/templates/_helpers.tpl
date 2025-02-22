{{/*
Expand the name of the chart.
*/}}
{{- define "templates.name" -}}
{{- if .Values.global.releaseName }}
{{- .Values.global.releaseName | trunc 63 | trimSuffix "-" }}
{{- else }}
{{- $name := default .Chart.Name .Release.Name }}
{{- $name | trunc 63 | trimSuffix "-" }}
{{- end }}
{{- end }}

{{/*
Create a default fully qualified app name.
We truncate at 63 chars because some Kubernetes name fields are limited to this (by the DNS naming spec).
If release name contains chart name it will be used as a full name.
*/}}
{{- define "templates.fullname" -}}
{{ include "templates.name" . }}
{{- end }}

{{/*
Create chart name and version as used by the chart label.
*/}}
{{- define "templates.chart" -}}
{{- printf "%s-%s" .Chart.Name .Chart.Version | replace "+" "_" | trunc 63 | trimSuffix "-" }}
{{- end }}

{{/*
Common labels
*/}}
{{- define "templates.labels" -}}
helm.sh/chart: {{ include "templates.chart" . }}
{{ include "templates.selectorLabels" . }}
{{- if .Chart.AppVersion }}
app.kubernetes.io/version: {{ .Chart.AppVersion | quote }}
{{- end }}
app.kubernetes.io/managed-by: {{ .Release.Service }}
{{- end }}

{{/*
Selector labels
*/}}
{{- define "templates.selectorLabels" -}}
app: {{ include "templates.fullname" . }}
version: {{ default .Chart.Version .Values.version | quote }}
app.kubernetes.io/name: {{ include "templates.name" . }}
app.kubernetes.io/instance: {{ include "templates.fullname" . }}
{{- end }}

{{/*
Create the name of the service account to use
*/}}
{{- define "templates.serviceAccountName" -}}
{{- if .Values.serviceAccount.create }}
{{- default (include "templates.fullname" .) .Values.serviceAccount.name }}
{{- else }}
{{- default "default" .Values.serviceAccount.name }}
{{- end }}
{{- end }}
