{{/* vim: set filetype=mustache: */}}
{{/*
Expand the name of the chart.
*/}}
{{- define "name" -}}
{{- default .Chart.Name .Values.nameOverride | trunc 63 | trimSuffix "-" -}}
{{- end -}}

{{- define "configMapName" -}}
{{ template "name" . }}-config
{{- end -}}

{{/*
Create chart name and version as used by the chart label.
*/}}
{{- define "chart" -}}
{{- printf "%s-%s" .Chart.Name .Chart.Version | replace "+" "_" | trunc 63 | trimSuffix "-" -}}
{{- end -}}

{{/*
Common labels
*/}}
{{- define "labels" -}}
helm.sh/chart: {{ include "chart" . }}
{{ include "selectorLabels" . }}
{{- if .Chart.AppVersion }}
app.kubernetes.io/version: {{ .Chart.AppVersion | quote }}
{{- end }}
app.kubernetes.io/managed-by: {{ .Release.Service }}
{{- end -}}

{{/*
Selector labels
*/}}
{{- define "selectorLabels" -}}
app.kubernetes.io/name: {{ include "name" . }}
{{- end -}}

{{/*
Create the name of the service account to use
*/}}
{{- define "serviceAccountName" -}}
{{- if .Values.serviceAccount.create -}}
    {{ default (include "name" .) .Values.serviceAccount.name }}
{{- else -}}
    {{ default "default" .Values.serviceAccount.name }}
{{- end -}}
{{- end -}}

{{/*
Create the configmap
*/}}
{{- define "buildConfigMap" -}}
apiVersion: v1
kind: ConfigMap
metadata:
  name: {{ template "configMapName" . }}
  labels:
    app.kubernetes.io/name: {{ include "name" . }}-config
    helm.sh/chart: {{ include "chart" . }}
    app.kubernetes.io/managed-by: {{ .Release.Service }}
{{- if .Values.configmap }}
{{- if .Values.configmap.mount }}
{{- if eq .Values.configmap.mount "file" }}
data:
{{ (.Files.Glob .Values.configmap.file).AsConfig | indent 2}}
{{- end -}}
{{- if eq .Values.configmap.mount "envjs" }}
data:
  env.js: |
    window._env={
{{- range .Values.configmap.variables }}
      "{{ .key }}": "{{ .value }}",
{{- end }}
    }
{{- end -}}
{{- end -}}
{{- end -}}
{{- end -}}