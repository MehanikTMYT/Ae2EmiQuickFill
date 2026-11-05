<div align="center">

<img src="src/main/resources/icon.png" alt="AE2: EMI Quick Fill" width="128" height="128">

# AE2: EMI Quick Fill

[English](README_EN.md) | 中文

</div>

---

在 AE2 终端旁边用 EMI 时，Shift+点击配方是填不进合成格的——得先点开配方详情，再点加号。这个模组把那一步去掉。

## 为什么本来不行

EMI 的 Shift+点击传的是 `CRAFTABLE`，而 AE2 的终端（合成终端、无线合成终端、样板编码终端）只对加号按钮的 `FILL_BUTTON` 跑自己的传输逻辑；`CRAFTABLE` 会退回到默认逻辑，要求材料全在背包里，材料放在 ME 网络里就直接判定合不了。

## 装了就有的效果

在任意 AE2 系终端里 Shift+点击 EMI 配方：

- 材料在 ME 网络里 → 填进合成格 / 编码成样板
- 材料不全 → 不会填进去，AE2 会提示哪些材料无法从网络提取（“按住 Ctrl 下单”是配方界面里加号按钮的交互，Shift+点击这条路上没有）

没有配置，也没有界面。纯客户端，服务端不用装。

## 能用的终端

- AE2 合成终端
- AE2 无线合成终端
- AE2 样板编码终端
- 其他继承 AE2 `AbstractRecipeHandler` 的第三方终端（没自己重写 `canCraft` 的自动生效；样板编码终端那种重写的已单独补上）

## 原理

一个 Mixin：在 `canCraft` 里把 `CRAFTABLE` 当成 `FILL_BUTTON`。`craft()` 本身不看类型，所以 `canCraft` 一过，后面跑的就是 AE2 原本那套填充分发逻辑，没有重写任何传输代码。

## 版本与分支

一个仓库，分支区分游戏版本，每个分支都是独立工程：

| 分支 | 游戏版本 | 加载器 |
|---|---|---|
| [`1.21.1`](../../tree/1.21.1) | 1.21.1 | NeoForge 21.1.x |
| [`1.20.1`](../../tree/1.20.1) | 1.20.1 | Forge 47.x |
| [`port/26.1.2`](../../tree/port/26.1.2) | 26.1.2 | NeoForge 26.1.2.x（Java 25） |

26.1.2 之前做不了：AE2 的 26.1 早期版本（26.1.12-beta）把 EMI 集成整个去掉了。**26.1.12-SNAPSHOT
起 EMI 集成回归**，但包从 `appeng.integration.modules.emi` 挪到了 `appeng.client.integrations.emi`
（AE2 自己的客户端代码里）。本分支的 Mixin 已经指向新包名，依赖走 mavenLocal 的
AE2 26.1.12-SNAPSHOT 和 EMI 1.1.24（fleet 构建）。详见 `PORTING.md`。

## 依赖

- Minecraft 26.1.2、NeoForge 26.1.2.x、Java 25
- AE2 26.1.x、EMI 1.1.24

1.21.1 分支需要 AE2 19.2.x、EMI 1.1.18+；1.20.1 分支需要 AE2 **15.4.10 或更新**——EMI 原生支持
是那一版才加进 AE2 的，更早的版本里没有被本模组打补丁的那段逻辑。

## 构建

```bash
JAVA_HOME=/usr/lib/jvm/java-25-openjdk-amd64 ./gradlew build
# 产物在 build/libs/
```

AE2 和 EMI 都从 mavenLocal 解析（fleet 本地构建，坐标见 `gradle.properties` 的 `ae2_version` /
`emi_version`），不需要手动放 jar。改依赖版本就改这两个属性。

## License

[MIT](LICENSE)
