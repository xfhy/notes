
原文:https://www.jianshu.com/p/2b22daa8e2f6

## 提供程序变更

从 Android P 开始，对于 AndroidOpenSSL (也称为 Conscrypt) 提供程序所重复的、来自于 BC 提供程序的部分功能，我们计划将予以弃用。

此改动仅会影响在调用 getInstance() 方法时明确指定 BC 提供程序的应用程序。

需要说明的一点是，我们此举的目的不是因为对 BC 提供程序的实施安全存在疑虑，而是因为重复功能会造成额外的成本和风险，却无法带来太多益处。

如果您在 getInstance() 调用中不会指定提供程序，则无需做出任何改动。

如果您按名称或实例指定提供程序 - 例如，Cipher.getInstance("AES/CBC/PKCS7PADDING", "BC") 或 Cipher.getInstance("AES/CBC/PKCS7PADDING", Security.getProvider("BC")) - 则 Android P 的行为将取决于您应用的目标 API 级别。

对于目标级别早于 P 的应用，调用会返回 BC 实施方法，并在应用日志中记录警告。对于目标级别为 Android P 或之后版本的应用，调用会抛出 NoSuchAlgorithmException。

为了解决此问题，您需要停止指定提供程序，并使用默认实施方法。

在后续的 Android 版本中，我们计划完全移除 BC 提供程序的弃用功能。在移除后，所有请求 BC 提供程序的调用 (不论按名称还是实例) 都会抛出 NoSuchAlgorithmException。

## 移除 Crypto 提供程序

在先前的帖子中，我们曾宣布自 Android Nougat 开始弃用 Crypto 提供程序。

此后，以 API 23 (Marshmallow) 或更早级别为目标的应用程序请求 Crypto 提供程序会成功，但以 API 24 (Nougat) 或之后级别为目标的应用程序请求则会失败。

在 Android P 中，我们计划完全移除 Crypto 提供程序。在移除后，所有对 SecureRandom.getInstance("SHA1PRNG", "Crypto") 的调用都会抛出 NoSuchProviderException。

请大家对自己的应用进行相应更新。

