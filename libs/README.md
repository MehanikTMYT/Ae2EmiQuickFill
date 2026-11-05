# 26.1.2 分支起，EMI 不再走本地 jar

EMI 用 mavenLocal 里的 fleet 构建（`dev.emi:emi-neoforge:1.1.24-semi151a6c88+26.1.2`，api
分类器用于编译），AE2 用 mavenLocal 的 `org.appliedenergistics:appliedenergistics2:26.1.12-SNAPSHOT`。
坐标见 `gradle.properties` 的 `ae2_version` / `emi_version`，本目录已不需要放 jar。
