# 大语言模型推理框架调研

# I. 引言

大语言模型（LLM）的迅猛发展及其在自然语言处理、代码生成、多模态交互等领域的广泛应用，对底层推理基础设施提出了前所未有的挑战。**模型规模的急剧膨胀（参数量从数十亿扩展至数万亿）和复杂计算需求（如注意力机制）导致推理过程中的显存占用巨大、计算延迟高昂**。为了在实际生产环境中高效、经济地部署 LLM，业界涌现出一系列专门针对 LLM 推理优化的框架。这些框架通过引入创新的内存管理机制、批处理策略、并行计算技术以及硬件加速等手段，旨在**提升推理吞吐量、降低延迟、优化资源利用率**。

本文主要对当前主流的大语言模型推理框架进行系统性调研与分析，将深入探讨各个框架的核心架构、设计理念、关键技术特点，并结合性能基准测试数据，分析其在不同模型规模和部署场景下的适用性。此外，本文还将覆盖框架对硬件的依赖与优化、部署复杂度、生态兼容性，并针对不同业务场景提供选型建议。最后是对 LLM 推理领域的前沿技术与未来趋势的展望，为工程师在技术选型上提供决策参考。

# II. LLM 推理的挑战与核心优化方向

LLM 推理面临的主要挑战源于其巨大的模型规模和自回归式的生成特性。这些挑战具体表现为：

- **显存瓶颈 (Memory Bottleneck)**：LLM 的参数量庞大，即使采用半精度（FP16/BF16）存储，数十亿参数的模型也需要数十 GB 的显存。更为关键的是，推理过程中广泛使用的键值**缓存（KV Cache）**会随着**批处理大小（Batch Size）**和**序列长度**的增加而急剧膨胀，成为显存占用的主要瓶颈。传统的 LLM 框架内存分配效率低下，常导致显存浪费和利用率不高。
- **计算瓶颈 (Compute Bottleneck)**：LLM 中的 Transformer 结构包含大量的矩阵乘法（GEMM）和注意力计算，对计算资源消耗巨大。尤其是在处理长序列或大批量请求时，计算延迟显著。
- **延迟与吞吐的权衡 (Latency vs. Throughput Trade-off)**：在服务化场景中，既要保证单个请求的低延迟（如聊天机器人追求快速响应），又要实现高吞吐以服务更多并发用户，这两者往往难以兼得 。
- **自回归解码的固有延迟 (Autoregressive Decoding Latency)**：LLM 通常采用自回归方式生成文本，即逐个 token 生成，每个 token 的生成依赖于先前所有 token。这一特性使得并行化生成变得困难，成为延迟的主要来源 。

针对以上挑战，LLM 推理框架的核心优化方向主要包括：

- **高效 KV 缓存管理 (Efficient KV Cache Management)**：通过分页注意力（PagedAttention）、块状缓存（Blocked KV Cache）等技术，优化 KV 缓存的存储和访问，减少内存碎片，提高显存利用率。
- **动态批处理/连续批处理 (Dynamic/Continuous Batching)**：替代传统的静态批处理，允许在批次处理过程中动态地加入新的请求、移除已完成的请求，从而提高 GPU 利用率，减少等待时间。
- 并行计算策略 (Parallelism Strategies)：
    - **张量并行 (Tensor Parallelism, TP)**：将模型层的权重和计算（如矩阵乘法）分割到多个 GPU 上并行处理，适用于单个 GPU 无法容纳模型层或希望加速层内计算的场景 。
    - **流水线并行 (Pipeline Parallelism, PP)**：将模型的不同层分配到不同的 GPU 或节点上，形成流水线处理，适用于模型深度过大，单 GPU 无法容纳整个模型的场景 。
    - **专家并行 (Expert Parallelism, EP)**：针对混合专家模型（MoE），将不同的专家（子网络）分布到不同设备上进行并行计算。
    - 其他如**序列并行、上下文并行**等针对特定场景的并行技术。
- **算子融合与优化内核 (Operator Fusion & Optimized Kernels)**：将多个计算操作融合成单个内核执行，减少 Kernel 启动开销和内存访问，利用如 FlashAttention 等高度优化的注意力实现来加速计算。
- **量化 (Quantization)**：将模型的权重和/或激活值从高精度浮点数（如 FP32、FP16）转换为低精度整数（如 INT8、INT4）或低精度浮点数（如 FP8），以减小模型体积、降低显存占用、加速计算，同时尽可能保持模型精度。
- **模型编译 (Model Compilation)**：将模型转换为硬件特定的优化执行引擎，如 NVIDIA TensorRT，通过图优化、内核选择等手段提升性能 。

这些优化方向的有效结合，是现代 LLM 推理框架提升效率的关键。

# III. 主流大模型推理框架

当前，业界涌现了多款主流的大模型推理框架，它们在设计理念、核心技术和适用场景上各有侧重。本文将重点分析以下几个具有代表性的框架：`vLLM、Text Generation Inference (TGI)、NVIDIA TensorRT-LLM、NVIDIA FasterTransformer、LMDeploy`，并简要介绍 `SGLang` 和 `OpenLLM` 等新兴或特定用途的框架。

### A. vLLM

`vLLM` 是一个由加州大学伯克利分校 `Sky Computing Lab` 发起并由社区驱动的高性能 LLM 推理服务引擎。它以其创新的内存管理机制 **PagedAttention** 著称，`vLLM`  主要解决了 LLM 推理中的显存瓶颈问题。

- **核心架构、设计理念与技术特点：**
    - **PagedAttention**：这是 `vLLM` 的核心技术。传统 LLM 推理中，KV 缓存通常以连续的内存块存储，导致严重的内部和外部碎片化。`PagedAttention` 借鉴了操作系统中虚拟内存和分页的思想，将 KV 缓存分割成非连续的物理块（页），逻辑上连续的 token 的 KV 对可以存储在物理上不连续的块中。这种方式显著减少了内存浪费，使得系统可以容纳更大的批次，处理更长的序列，并支持更灵活的内存共享。例如，在序列并行或束搜索（Beam Search）等场景下，不同序列间可以共享相同的 token 前缀，`PagedAttention` 能够高效地实现这种共享，避免了冗余存储和拷贝。
    - **连续批处理 (Continuous Batching)**：`vLLM` 实现了高效的连续批处理机制。与传统静态批处理在整个批次完成后才处理新请求不同，连续批处理在每个迭代步骤中动态地将已完成的序列替换为新的待处理序列。这使得 GPU 能够持续保持高利用率，显著提升吞吐量并降低平均延迟。
    - **优化的 CUDA 核函数**：`vLLM` 包含优化的 CUDA 核函数，并集成了如 `FlashAttention` 和 `FlashInfer` 等业界领先的注意力优化库，以加速模型执行。
    - **分布式推理**：支持张量并行（Tensor Parallelism），允许将大型模型分片到多个 GPU 上运行，从而支持单 GPU 无法容纳的超大规模模型 。`vLLM` 也支持流水线并行（Pipeline Parallelism），可以将模型的不同层分布在多个节点上。
    - **量化支持**：`vLLM` 支持多种量化方案，包括 `GPTQ、AWQ、SqueezeLLM (INT8 W8A8)、FP8 KV 缓存`等 。这有助于减小模型体积，降低显存占用，并加速推理。
    - **投机解码 (Speculative Decoding)**：`vLLM` 支持投机解码，可以使用一个较小的`草稿模型`快速生成候选 token，然后由主模型并行验证，从而在某些情况下减少每个 token 的生成延迟。支持使用**草稿模型、N-gram 匹配或 MLP 推测器**进行推测。
    - **LoRA 支持**：`vLLM` 支持 LoRA (Low-Rank Adaptation) ，包括在服务启动时加载多个 LoRA 模块，以及通过 API 动态加载和卸载 LoRA 适配器。这使得单个基础模型可以高效地服务于多个任务或用户，显著节省资源。
- **硬件支持：**
    - **NVIDIA GPU**：`vLLM` 的主要优化目标，提供最佳性能。
    - **AMD GPU**：通过 ROCm 软件栈支持 AMD GPU。
    - **Intel GPU**：通过 Intel Extension for PyTorch 支持 Intel Arc 和 Data Center Max 系列 GPU，利用 vLLM v1 引擎的特性如 chunked_prefill。
    - **CPU 推理**：`vLLM` 支持在多种 CPU 架构上进行推理，包括 x86 (Intel/AMD)、ARM64 (支持 NEON)、Apple Silicon (macOS) 和 IBM Z (s390x)。支持的数据类型包括 `FP32、FP16、BF16`（具体支持依架构而定）。CPU 推理需要从源码编译，并提供了一些性能调优建议，如使用 TCMalloc、合理设置 OpenMP 线程绑定和 KV 缓存大小。在 CPU 上也支持张量并行和部分量化方法。
- **部署复杂度与生态支持：**
    - **安装**：可以通过 pip 安装，也支持从源码编译以获得对特定硬件（如 CPU）的支持或最新特性。
    - **API**：提供与 OpenAI 兼容的 API 服务器接口，方便集成到现有应用中。
    - **Hugging Face 集成**：与 Hugging Face Transformers 模型库紧密集成，可以轻松加载和运行 Hugging Face Hub 上的模型。
    - **BentoML 集成**：可以与 BentoML 结合部署，将 vLLM 作为后端，打包成 OCI 兼容镜像并部署到 Kubernetes 等环境。
    - **KServe 集成**：KServe 的 Hugging Face 运行时默认可以使用 vLLM 作为后端来服务 LLM 模型，以获得更好的性能。
    - **ONNX 支持**：vLLM 主要处理 PyTorch 模型。虽然可以将模型转换为 ONNX 格式，但 vLLM 本身并不直接执行 ONNX 模型；其优化是针对 PyTorch 模型的。关于 vLLM 是否能直接消费 ONNX 模型进行其 PagedAttention 等优化的信息较少，通常的流程是 vLLM 处理原始的 Hugging Face 模型格式。
    - **多实例部署**：通过张量并行和流水线并行支持多 GPU 和多节点部署。结合 Nginx 等负载均衡器可以实现多 vLLM 实例的水平扩展 。
- **GitHub 活跃度**：vLLM 是一个非常活跃的开源项目，拥有大量的 Stars、Forks 和 Issues，社区贡献积极。

`vLLM` 的设计哲学是最大化利用 GPU 显存和计算资源，通过 `PagedAttention` 和连续批处理等核心技术，在不牺牲模型准确性的前提下，大幅提升 LLM 推理的吞吐量和效率。这使得它成为许多对性能有高要求的 LLM 服务场景下的首选框架之一。其对多种硬件平台（包括 CPU 和非 NVIDIA GPU）的逐步支持，也显示了其拓展应用范围的努力。

### B. Text Generation Inference (TGI)

Text Generation Inference (TGI) 是由 Hugging Face 开发和维护的，专为大规模语言模型设计的生产级推理解决方案。它旨在提供高效、低延迟、高吞吐的文本生成服务，并与 Hugging Face 生态系统紧密集成。

- **核心架构、设计理念与技术特点：**
    - **两阶段推理 (Prefill & Decode)**：TGI 清晰地划分了 LLM 推理的两个主要阶段：Prefill（预填充）阶段处理输入提示（prompt），Decode（解码）阶段自回归地生成后续 token。这两个阶段具有不同的计算特性，Prefill 计算密集度高，而 Decode 则更受内存带宽限制。
    - **连续批处理 (Continuous Batching)**：TGI 的核心组件之一是其 `router`，它负责管理传入请求并实现智能的连续批处理算法 。`router` 动态地将新请求添加到正在运行的批次中，以优化 GPU 利用率并防止内存溢出（OOM）错误。其连续批处理逻辑是用 Rust 语言编写的，以追求高性能和并发能力。
    - **KV 缓存管理 (Paged Attention, Flash Attention)**：为了优化解码阶段的性能，TGI 广泛采用 KV 缓存技术。它支持 PagedAttention，将 KV 缓存分割成页面进行管理，允许动态分配和释放，从而提高内存效率。同时，TGI 也集成了 FlashAttention，这是一种高效的注意力机制，可以处理无填充（padless）的张量，减少显存占用和内存移动，尤其在处理长序列时效果显著。
    - **模型分片 (Sharding / Tensor Parallelism)**：TGI 支持多 GPU 推理，可以将模型权重分片到多个 GPU 上（张量并行），以支持单 GPU 无法容纳的大型模型或加速推理 。
    - **量化支持 (Quantization)**：TGI 支持多种量化技术，如 GPTQ, AWQ, EETQ (一种数据无关的量化方法) 以及 FP8 精度（特别是在 Gaudi 硬件上通过 Intel Neural Compressor）。
    - **投机解码 (Speculative Decoding)**：TGI 支持投机解码以加速生成速度。
    - **流式响应 (Token Streaming)**：支持将生成的 token 以流式方式返回给客户端，提升交互体验。
    - **多模态支持**：TGI 正在扩展对视觉语言模型 (VLM) 的支持，例如在 Gaudi 硬件上的集成。
- **硬件支持：**
    - **NVIDIA GPU**：TGI 广泛支持并优化了在 NVIDIA GPU 上的运行。
    - **AMD GPU**：支持通过 ROCm 软件栈在 AMD GPU 上运行 。
    - **Intel Gaudi (HPU)**：TGI 已经原生集成了对 Intel Gaudi1, Gaudi2, 和 Gaudi3 加速器的支持。这包括了对多卡推理、视觉语言模型和 FP8 精度的支持。
    - **AWS Inferentia**：TGI 支持在 AWS Inferentia 芯片上部署。
    - **CPU 推理**：TGI 主要为 GPU 和其他加速器优化。虽然其核心组件（如 Rust 编写的 router）和 tokenizer 逻辑可能在 CPU 上运行，但高性能 LLM 推理本身是面向加速器的。官方文档和主流讨论未突出 TGI 的专用 CPU 高性能推理模式。
- **部署复杂度与生态支持：**
    - **Hugging Face 生态系统**：作为 Hugging Face 的官方产品，TGI 与 Hugging Face Hub 上的模型无缝集成，大多数主流开源 LLM 在发布当天即可在 TGI 中使用。
    - **部署方式**：通常通过 Docker 容器进行部署，Hugging Face 提供了官方的 TGI Docker 镜像。
    - **监控与可观测性**：通过 OpenTelemetry 支持分布式追踪，并提供 Prometheus 指标用于全面的监控。
    - **KServe 集成**：在 KServe 上部署 Hugging Face 模型时，可以使用 TGI 作为运行时。如果特定模型或特性 TGI 不直接支持，KServe 可能会回退到 vLLM 等其他后端。
    - **ONNX Runtime 执行**：TGI 主要处理 PyTorch 模型。没有直接证据表明 TGI 内部使用 ONNX Runtime 来执行模型。ONNX Runtime 本身支持通用 AI 模型的推理，包括一些生成式 AI 模型，但这与 TGI 的核心推理引擎是不同的。
    - **Hugging Face Inference Endpoints**：TGI 是 Hugging Face Inference Endpoints 服务背后的核心技术之一，用户可以通过该服务轻松部署模型而无需管理底层基础设施。
- **GitHub 活跃度**：TGI 是一个活跃的开源项目，拥有相当数量的关注和贡献。

TGI 的一个显著特点是其在 Hugging Face 生态系统中的核心地位，这为用户提供了极大的便利性，可以快速部署和尝试最新的开源模型。其架构设计兼顾了性能（通过连续批处理、PagedAttention、FlashAttention 等）和生产环境的易用性（如 Docker化部署、监控集成）。TGI 对多种硬件加速器（NVIDIA、AMD、Intel Gaudi）的支持也体现了其致力于提供更广泛部署选择的努力。Rust 语言在核心请求处理部分的应用，也反映了业界在性能关键组件上从 Python 转向系统级编程语言以获得更佳效率和并发能力的趋势。

### C. NVIDIA TensorRT-LLM

`NVIDIA TensorRT-LLM` 是 NVIDIA 推出的一款开源库，利用 NVIDIA GPU 加速和优化最新大语言模型的推理性能。它构建于 TensorRT 之上，提供了一个 Python API，封装了 `TensorRT` 的深度学习编译器、源自 FasterTransformer 的优化内核、预处理和后处理逻辑，以及多 GPU/多节点通信机制 。其核心理念是将模型权重（例如，来自 ONNX 格式）编译成针对特定 NVIDIA GPU 架构高度优化的可执行“引擎” 。

- **核心架构、设计理念与技术特点：**
    - **模型编译与优化**：`TensorRT-LLM` 的核心流程是将训练好的 LLM（通常先转换为 ONNX 格式，或直接从支持的 Python 模型定义）编译成 TensorRT 引擎。在编译过程中，`TensorRT-LLM` 会应用一系列优化，包括`fusing layers and tensors`、精度校准（FP16, BF16, INT8, 以及在 Hopper 及更新架构上支持的 FP8）、算子自动调优（kernel auto-tuning）和权重重排等。
    - **即时批处理 (In-flight Batching / Continuous Batching)**：`TensorRT-LLM` 实现了高效的即时批处理，也称为连续批处理或迭代级批处理。该技术动态管理请求批次，当批次中某个序列生成完成时，会立即将其从批次中移除，并填入新的待处理请求，从而最大化 GPU 利用率，减少请求排队等待时间，消除对请求进行填充（padding）的需求。
    - **Paged KV Cache / PagedAttention**：支持 `PagedAttention` 技术，用于高效管理 KV 缓存，减少内存碎片，提升内存利用率，支持更大的批处理规模和更长的序列长度。
    - **优化的 CUDA 内核**：集成了来自 `FasterTransformer` 的高度优化 CUDA 内核，并结合 TensorRT 自身的内核优化能力，以实现极致的计算性能 。
    - **量化 (Quantization)**：提供广泛的量化支持，包括 FP16、BF16、INT8（权重和/或激活值）、以及在 NVIDIA Hopper 及更新架构 GPU 上支持的 FP8 格式。量化可以显著减小模型尺寸，降低显存占用，并加速推理 。
    - **并行计算 (Parallelism)**：内置对多种并行策略的支持，包括张量并行（Tensor Parallelism）、流水线并行（Pipeline Parallelism）和专家并行（Expert Parallelism），以实现跨多 GPU 和多节点的模型部署与高效推理 。
    - **投机解码 (Speculative Decoding)**：支持多种投机解码算法，如 Medusa、ReDrafter、Lookahead Decoding 和 Eagle，以进一步降低生成延迟 。
    - **LoRA 支持 (Low-Rank Adaptation)**：原生支持在推理时服务多个 LoRA 适配器，且可以与单个量化后的基础模型检查点（checkpoint）结合使用。支持并发处理来自不同 LoRA 适配器的请求，或单个请求使用多个 LoRA 适配器（批处理模式）。
    - **分块上下文 (Chunked Context)**：支持将长上下文分割成多个块进行处理，有助于提高长序列任务的吞吐量 。
- **硬件支持：**
    - **NVIDIA GPU**：TensorRT-LLM 专为 NVIDIA GPU 设计和优化，支持 Volta、Turing、Ampere、Ada Lovelace 和 Hopper 等较新的 GPU 架构。在 H100 等最新 GPU 上性能提升尤为显著。
    - **CPU / 其他加速器**：不支持。TensorRT-LLM 是一个针对 NVIDIA GPU 的特定解决方案。Windows 平台的原生支持已在 v0.18.0 版本后弃用。
- **部署复杂度与生态支持：**
    - **NVIDIA Triton Inference Server**：TensorRT-LLM 模型通常通过 Triton Inference Server 的 TensorRT-LLM 后端进行服务化部署。该后端采用 C++ 实现，并利用 executor API 来支持即时批处理等高级特性 。
    - **NVIDIA NeMo 框架**：TensorRT-LLM 是 NVIDIA NeMo 端到端生成式 AI 部署框架的组成部分。
    - **NVIDIA NIM (NVIDIA Inference Microservices)**：NIM 微服务使用 TensorRT-LLM 作为其后端推理引擎之一。
    - **ONNX**：TensorRT-LLM 可以将 ONNX 格式的模型作为输入，编译成优化的 TensorRT 引擎。这是其实现跨框架模型导入的主要途径之一。
    - **KServe**：Triton Inference Server 支持 KServe API，因此通过 Triton 部署的 TensorRT-LLM 模型可以间接获得 KServe 的支持。
    - **BentoML**：未发现官方文档明确提供 TensorRT-LLM 与 BentoML 的直接深度集成方案。BentoML 的 LLM 示例通常以 vLLM 或 Hugging Face Transformers 为主 。理论上，由于 Triton 可以作为 BentoML 的运行器（runner），因此可能存在间接的集成路径。
    - **模型支持**：支持包括 Llama、Mistral、Qwen 在内的多种主流 LLM 家族。
- **多实例/多节点能力：**
    - TensorRT-LLM 具备强大的多 GPU 和多节点推理能力，通过 MPI（Message Passing Interface）进行通信协调 。
    - 支持领导者模式（Leader Mode）和编排者模式（Orchestrator Mode）来管理多实例运行。
- **GitHub 活跃度**：项目活跃还可以。

`TensorRT-LLM` 体现了 NVIDIA 期望最大化其硬件平台性能的策略，通过提供一个高度优化但与供应商生态系统紧密绑定的软件层。这种策略为 NVIDIA 生态系统内的用户带来了强大的性能优势，但也可能使用户在硬件选择上受到一定限制。其“引擎”编译步骤是实现深度优化的关键，允许进行积极的算子融合和硬件特定调整。然而，这也意味着与那些直接加载原始模型权重的框架相比，模型迭代和部署的灵活性可能稍逊一筹。如果模型结构、GPU 类型或关键推理参数发生变化，可能需要重新编译引擎，这在快速实验和迭代的场景下可能成为一个考量点。尽管如此，对于追求极致性能和已经投入 NVIDIA 生态的用户而言，TensorRT-LLM 及其与 Triton 的结合提供了业界领先的推理解决方案。

### D. NVIDIA FasterTransformer

`NVIDIA FasterTransformer` 是一个用 `C++/CUDA` 编写的加速引擎，专注于 Transformer 模型的推理优化，特别是针对在分布式环境中跨多 GPU 和多节点运行的大型模型。它通过重写整个模型（或关键部分）为纯 `C++/CUDA` 代码来实现加速，并利用高度优化的库（如 cuBLAS, cuBLASLt）和自定义内核。

- **核心架构、设计理念与技术特点：**
    - **高度优化的 CUDA 内核**：为 Transformer 中的关键层（如 GEMM、自注意力机制）提供专门优化的内核。一个显著特点是 GEMM 内核的自动调优（autotuning），可以根据矩阵大小和 GPU 架构动态调整内核参数以优化性能。
    - **模型并行**：支持张量并行（Tensor Parallelism）和流水线并行（Pipeline Parallelism），以处理无法放入单个加速器内存的大型模型。
    - **KV 缓存优化**：在每个解码步骤中分配缓冲区来存储先前计算的键（K）和值（V）状态，避免重复计算，从而节省计算成本和每步重新分配缓冲区的开销。
    - **量化支持**：支持 FP32、FP16、BF16 等浮点精度，以及 INT8 权重量化（INT8 weight-only Post-Training Quantization, PTQ）。此外，还包含对 FP8 的实验性支持。INT8 权重量化通常在 FP16/BF16 计算模式下工作，并且主要针对小型批次和大型权重矩阵的场景提供性能优势。
    - **内存优化**：通过在不同的解码器层之间重用内存缓冲区来减少内存占用，这对于层数非常多的大模型（如 GPT-3 有 96 层）尤其重要，理论上可以将这部分内存需求降低到原来的 1/96。
    - **动态随机种子与停止符**：支持在生成过程中使用动态随机种子，并能够根据停止符（stop tokens）来终止生成。
    - **束搜索与采样**：同时支持束搜索（beam search）和采样（sampling）两种解码策略。
- **硬件支持：**
    - **NVIDIA GPU**：FasterTransformer 专为在 NVIDIA GPU 上高效运行而设计。
    - **CPU / 其他加速器**：不是其主要优化目标。有一个名为 xFasterTransformer 的独立项目，由 Intel 主导，专注于 x86 CPU 平台的 Transformer 推理优化。FasterTransformer 本身未提及对 Intel Gaudi 或 FPGA 的特定支持。
- **部署复杂度与生态支持：**
    - **检查点转换器 (Checkpoint Converters)**：提供工具用于转换来自 HuggingFace Transformers、Megatron-LM、NVIDIA NeMo 以及 TensorFlow 的模型检查点，使其兼容 FasterTransformer 的格式。
    - **框架集成**：可以与 TensorFlow 和 PyTorch 等主流深度学习框架结合使用。
    - **Triton Inference Server 集成**：FasterTransformer 可以作为 NVIDIA Triton Inference Server 的后端部署，从而利用 Triton 的服务化能力。
    - **Amazon SageMaker**：已预先集成到 Amazon SageMaker 的大规模模型推理（LMI）深度学习容器（DLCs）中，简化了在 SageMaker 上的部署流程。
    - **ONNX 支持**：支持将模型转换为 ONNX 格式，或从 ONNX 格式导入模型。
    - **KServe / BentoML**：官方文档中未直接提及与 KServe 或 BentoML 的深度集成。若要在这些平台上使用，通常会通过其 Triton 后端进行部署。例如，TorchServe（可以集成 FasterTransformer）支持 KServe。
- **多实例/多节点能力：**
    - 支持多 GPU 和多节点推理，利用 MPI 进行多节点间的通信，并使用多线程控制单节点内的多个 GPU。
- **GitHub 活跃度**：相较于 vLLM 或 TensorRT-LLM 等新兴框架，活跃度一般。

`FasterTransformer` 在 Transformer 模型推理优化领域扮演了重要的奠基角色，其提供的许多高性能 CUDA 内核和并行化思想，已被后续更全面的框架（如 TensorRT-LLM）所吸收或借鉴。例如，TensorRT-LLM 明确提到其包含了来自 FasterTransformer 的优化内核 。虽然 FasterTransformer 本身是一个功能强大的底层库，但其核心技术贡献现在往往通过更高级、集成度更高、功能更丰富的框架被用户间接使用。近年来其在 GitHub 上的活动相对减少，可能也反映了这一趋势。

另外，xFasterTransformer 作为一个针对 x86 CPU 的并行项目独立发展，也凸显了这样一个事实：即便是针对相同的模型架构（Transformer），在根本不同的硬件平台（GPU vs CPU）上实现高性能推理，需要截然不同的优化策略和代码库。CPU 架构（如 x86、ARM）拥有不同的指令集、内存层级和并行能力，GPU 上行之有效的优化（如基于 CUDA 核的大规模并行）并不能直接照搬到 CPU。xFasterTransformer 专注于利用 Xeon 等 CPU 的硬件特性（如 AVX512 指令集），表明其优化路径是独立于 GPU 版本的 FasterTransformer 的。

### E. LMDeploy

`LMDeploy` 是由 OpenMMLab 的 MMRazor 和 MMDeploy 团队推出的一款专注于 LLM 高效部署的工具包。它的一大特色是提供了双推理引擎架构：**TurboMind 引擎**和**PyTorch 引擎**。TurboMind 引擎采用 `C++/CUDA` 实现，追求极致的推理性能；而 PyTorch 引擎则纯粹使用 Python 开发，旨在降低开发门槛，方便开发者进行模型适配和新功能研发。

- **核心架构、设计理念与技术特点：**
    - TurboMind 引擎：
        - **持续批处理 (Persistent Batching / Continuous Batching)**：高效管理请求批次，提升 GPU 利用率 。
        - **阻塞式 KV 缓存 (Blocked KV Cache)**：优化 KV 缓存的内存布局和管理，以存储更多上下文或支持更大批次 。
        - **动态拆分与融合 (Dynamic Split & Fuse)**：一种提高处理效率的技术 。
        - **张量并行 (Tensor Parallelism)**：支持将模型层内计算分布到多个 GPU 。
        - **高性能 CUDA 内核**：替换原生的 Torch 实现，使用高度优化的 CUDA 内核执行关键运算。
        - **量化**：支持权重量化（Weight-only）和 K/V 缓存量化。其 4-bit 推理性能据称比 FP16 高出 2.4 倍，并通过 OpenCompass 评估验证了量化质量 。
    - PyTorch 引擎：
        - **架构**：包含 `Engine`、`EngineInstance`、`ModelAgent` 和 `Scheduler` 等核心组件。`EngineInstance` 发送请求，`Engine` 负责调度和执行。`ModelAgent` 封装模型加载、缓存管理和张量并行。`Scheduler` 管理序列和资源分配，采用类似 vLLM 的分页式 KV 缓存管理策略。
        - **持续批处理**：通过将批内所有序列连接成一个长序列来避免填充，从而减少内存占用和不必要的计算。
        - **张量并行**：支持多设备运行大型模型。
        - **S-LoRA**：支持 S-LoRA 技术，允许在有限显存下高效服务多个 LoRA 适配器。适配器按需分页和交换，并开发了专用内核支持未合并适配器的推理。
        - **量化**：PyTorch 引擎支持 W8A8（权值 8-bit，激活值 8-bit）量化。
    - 通用特性：
        - **交互式推理模式**：通过缓存多轮对话过程中的 K/V 状态，引擎能够“记住”对话历史，避免重复处理历史会话，提升多轮对话效率 。
        - **多模态支持**：提供针对视觉语言模型（VLM）的推理流水线，支持处理图像数据。
        - **兼容性**：支持同时使用 KV 缓存量化、AWQ（Activation-aware Weight Quantization）和自动前缀缓存（Automatic Prefix Caching）。
- **硬件支持：**
    - **NVIDIA GPU**：LMDeploy 的主要优化目标，兼容 Volta (V100), Turing (20 系列, T4), Ampere (30 系列, A10, A100等), 以及 Ada Lovelace (40 系列) 架构的 NVIDIA GPU，最低 CUDA 版本要求 11.3。
    - **其他硬件**：官方文档主要强调对 NVIDIA GPU 的支持。PyTorch 引擎由于其 Python 实现，理论上具备一定的 CPU 兼容性，但性能优化重点在 GPU。未在核心摘要中明确提及对 AMD GPU、Intel GPU/Gaudi 或 FPGA 的特定深度优化。
- **部署复杂度与生态支持：**
    - **安装**：推荐使用 pip 在 conda 环境中安装。也支持从源码编译，特别是使用 TurboMind 引擎时。
    - **API 服务**：可以启动 API 服务器，提供与 OpenAI 兼容的接口（如 `/v1/chat/completions`），方便客户端调用。
    - **Hugging Face 模型兼容**：支持加载和运行 Hugging Face Hub 上的模型。
    - **KServe 集成**：主要文档片段中未直接提及官方的 KServe 集成方案。一个 GitHub issue 提到了在昇腾（Ascend）上通过 K8s 部署的疑问。
    - **BentoML 集成**：存在一个名为 `BentoLMDeploy` 的示例项目，展示了如何结合 LMDeploy 和 BentoML 进行部署。可参考通用的 BentoML 文档进行部署。
    - **ONNX Runtime 支持**：LMDeploy 是 MMDeploy 的一部分，而 MMDeploy 提供了对 ONNX Runtime 自定义算子的支持。然而，LMDeploy 的核心推理引擎（TurboMind 和 PyTorch Engine）是独立于 ONNX Runtime 的高度优化实现，并非主要通过 ONNX Runtime 执行 LLM 推理。
    - **投机解码**：在提供的核心摘要中，未明确提及 LMDeploy 的 TurboMind 或 PyTorch 引擎支持投机解码功能。
- **多实例/多节点能力：**
    - LMDeploy 提供了“便捷的分布式服务器”（Effortless Distribution Server），利用请求分发服务，可以方便高效地将多模型服务部署到多机多卡环境 。张量并行本身也支持多 GPU 配置。

`LMDeploy` 采用双引擎策略，试图平衡极致性能（TurboMind）与开发便捷性及可扩展性（PyTorch 引擎）。这种设计满足了不同用户的需求：一部分用户可以直接利用 TurboMind 获得针对 NVIDIA GPU 的深度优化性能，另一部分研究者或开发者则可以利用 PyTorch 引擎的灵活性快速迭代、支持新模型或实现自定义功能，并有可能将成熟的优化迁移到高性能引擎中。其对多种量化技术（如 4-bit、W8A8、K/V 缓存量化）和 S-LoRA 的重点支持，显示出 LMDeploy 在优化资源受限环境下的推理效率以及支持多任务服务场景方面的努力。这对于需要在有限硬件预算下提供多样化 LLM 服务或最大化硬件投资回报率的场景具有重要意义。

### F. SGLang

`SGLang (Structured Generation Language) `是一个为大语言模型（LLM）和视觉语言模型（VLM）设计的高速服务框架，由 LMSYS Org（也开发了 Vicuna 和 Chatbot Arena）推出。其核心设计理念是通过共同设计后端运行时（Runtime）和前端语言（Frontend Language），来实现对模型交互的更快速度和更强控制力。

- **核心架构、设计理念与技术特点：**
    - 高效后端 runtime：
        - **RadixAttention**：`SGLang` 的一项创新技术，用于跨多个生成调用实现高效的 KV 缓存重用 。与 vLLM 的 PagedAttention 主要优化单个长序列的缓存管理不同，RadixAttention 允许 SGLang 在不同查询之间重用和共享缓存内容，避免冗余计算 。
        - **零开销 CPU 调度器 (Zero-Overhead CPU Scheduler)**：实现高效的请求批处理调度。其基于 Python 的调度器在性能上常能媲美甚至超越基于 C++ 的系统。
        - **连续批处理 (Continuous Batching) 与 Paged Attention (Token Attention)**：采用了这些业界标准的 LLM 服务优化技术，以提升吞吐量和内存效率。
        - **优化内核与编译**：集成了来自 FlashInfer 的高性能 CUDA 内核，并利用 `torch.compile` (源自 gpt-fast) 进行模型编译加速 。
        - **量化 (Quantization)**：支持动态量化，包括 FP8、INT4 (W4A16)、AWQ、GPTQ 等多种量化方案，以在硬件上获得最大速度。
        - **投机解码 (Speculative Decoding)**：支持该技术以加速 token 生成。
        - **张量并行 (Tensor Parallelism)**：支持多 GPU 推理。
        - **分块预填充 (Chunked Prefill)**：优化长输入序列的处理。
        - **结构化输出 (Structured Outputs / xGrammar)**：通过使用压缩的有限状态机 (FSM)，SGLang 可以高效地生成符合特定格式（如 JSON）的结构化输出。
        - **多 LoRA 批处理 (Multi-LoRA Batching)**：支持高效服务多个 LoRA 适配器。
    - 灵活的前端语言 (Python DSL)：
        - 提供直观的 Python 接口用于编写 LLM 应用程序，支持链式生成调用、高级提示工程、控制流（循环、条件）、多模态输入、并行执行以及与外部工具的交互。
    - 预填充-解码分离 (Prefill-Decode Disaggregation)：
        - 将预填充（处理输入提示）和解码（生成 token）两个阶段分离，允许对每个阶段进行针对性优化。该设计通过在预填充服务器和解码服务器之间交错执行，实现了大规模专家并行（EP），并显著提升了吞吐量。
- **硬件支持：**
    - **NVIDIA GPU**：SGLang 的主要优化目标平台。
    - **AMD GPU**：近期已添加对 AMD GPU 的支持。
    - **CPU 推理**：SGLang 提及其拥有一个“零开销 CPU 调度器” 75，这表明其架构考虑了 CPU 上的调度。然而，其性能优化的重点明显在于 GPU 和其他加速器。提供的文档片段中未包含关于纯 CPU 推理模式的详细性能数据、特定设置指南或局限性说明。
- **部署复杂度与生态支持：**
    - **模型支持**：支持广泛的生成模型（如 `Llama, Gemma, Mistral, Qwen, DeepSeek, LLaVA`）、嵌入模型（e5-mistral, gte）和奖励模型（Skywork），并易于扩展以集成新模型。
    - **集成**：SGLang 构建于多个开源 LLM 引擎（如 `LightLLM, vLLM, Guidance`）的基础之上 ，并已集成到 PyTorch 生态系统中。
    - **部署**：可用于服务化部署，已被 `xAI、NVIDIA、Google Cloud `等多家知名企业和机构采用，全球有超过 10 万块 GPU 运行 SGLang 的生产部署。
- **多实例/多节点能力：**
    - 支持张量并行。其预填充-解码分离架构结合 DeepEP（大规模专家并行）也表明其具备多节点部署和协调能力。
- **GitHub 活跃度**：非常活跃的开源项目。

`SGLang` 的一个核心差异化优势在于其前端 DSL 与高度优化的后端运行时的协同设计。这种设计使其特别适合处理复杂的、多步骤的 LLM 应用，例如智能体（agent）系统或需要严格结构化数据（如 JSON）生成的场景。在这些场景中，对生成流程的控制和中间状态的管理与原始 token 的生成速度同等重要。传统的推理服务接口通常是简单的文本输入、文本输出模式，而 SGLang 的前端语言允许开发者通过编程方式精确控制生成逻辑，例如实现循环、条件判断、调用外部工具等。其后端通过 RadixAttention 实现跨请求的 KV 缓存重用 ，以及通过 xGrammar 实现受约束的生成，从而高效执行这些复杂任务。这使得 SGLang 超越了单纯优化单轮次文本生成的范畴。

尽管 SGLang 相对较新，但它已被多家主要行业参与者采纳，并融入 PyTorch 生态系统，这表明市场对其独特结合高性能与高可编程性的强烈需求。它可能填补了那些纯粹追求原始推理速度的框架，或那些控制机制不够灵活/高效的框架所留下的空白。SGLang 通过借鉴 vLLM、FlashInfer 等开源项目的优秀组件，并在其上构建了显著的独特价值，显示出其在 LLM 推理基础设施领域强大的发展潜力。

### G. 其他值得关注的框架/库

除了上述主流框架外，还有一些其他值得关注的推理解决方案，它们或专注于特定优化方向，或作为更底层的基础库存在。

- **DeepSpeed-Inference:**
    - 作为微软 DeepSpeed 库的一部分，`DeepSpeed-Inference` 专注于大模型的推理加速。
    - 其核心技术包括多种并行策略（张量并行、流水线并行、专家并行、ZeRO 并行）、自定义的高性能推理内核、通信优化以及对异构内存（`CPU RAM、GPU HBM、NVMe SSD`）的利用。它还集成了 ZeroQuant、XTC 等模型压缩技术。
    - 硬件支持方面，主要针对 NVIDIA GPU（Pascal 及更新架构）和 `AMD GPU (MI100, MI200)` 。DeepSpeed 也支持在 Intel Gaudi 上进行训练和推理。虽然有针对 CPU 使用的通用建议，但其核心优势在于利用加速器和异构内存系统。
    - MII (Model Implementations for Inference) 是建立在 `DeepSpeed-Inference `之上的一个抽象层，旨在简化模型的低延迟、高吞吐量部署。
    - DeepSpeed-Inference 的优势在于其处理超大规模模型的能力，这得益于其源自大规模训练的优化技术，如 ZeRO 系列优化器对模型状态和激活的有效管理和卸载。
- **ONNX Runtime:**
    - `ONNX Runtime` 是一个跨平台的推理和训练加速器，提供一个通用的模型执行后端。
    - 它通过不同的执行提供程序（Execution Providers, EPs）支持多种硬件，包括 `CPU、NVIDIA GPU (CUDA, TensorRT EPs)、AMD GPU (ROCm EP)、Intel GPU/Gaudi`等 。
    - ONNX Runtime 支持包括 `Llama、Mistral、Phi、Stable Diffusion、Whisper` 在内的多种生成式 AI 模型。
    - 它可以部署在云端、边缘设备、移动设备和 Web 浏览器中。
    - ONNX Runtime 的核心价值在于其作为通用部署目标的角色，使得在一个框架中训练的模型（如 PyTorch）可以转换为 ONNX 格式，然后在多样化的硬件上通过 ONNX Runtime 运行，并可能获得硬件加速。它本身并非一个针对 LLM 设计的端到端优化引擎，而是依赖于 ONNX 格式和各硬件厂商提供的 EP 来实现加速。
- **PyTorch 原生解决方案 (TorchAO, GemLite):**
    - PyTorch 自身也在不断增强其原生 LLM 推理能力，以减少对外部专用框架的依赖。
    - **TorchAO** 是一个 PyTorch 原生库，提供量化（支持 int4, FP8 等低精度）、稀疏化和张量并行（通过 DTensor 实现）的功能，API 设计简洁，并可与 `torch.compile` 等 PyTorch 特性组合使用。
    - **GemLite** 是一个基于 Triton 的内核库，专注于优化低比特矩阵乘法运算（支持 fp16, int8, fp8 以及 int4/2/1 等压缩格式），提供自动调优功能，并与 `torch.compile` 兼容。
    - PyTorch 正在积极地将更多推理优化能力整合到其核心生态系统中。这对于已经熟悉 PyTorch 的用户来说，可以简化开发工作流，使得从训练到推理的过渡更加平滑。

这些框架和库各有侧重，共同构成了 LLM 推理优化的丰富生态。选择时需根据具体需求，如模型规模、硬件平台、性能目标（延迟、吞吐）、部署环境以及开发团队的技术栈等因素综合考量。

# IV. 性能分析与比较基准

对 LLM 推理框架进行性能评估是技术选型的关键环节。本节将定义关键性能指标（KPIs），探讨影响基准测试的因素，并综合现有数据对主流框架进行比较。

- **关键性能指标 (KPIs) 定义：**

    - 吞吐量 (Throughput)：
        - **每秒处理 Token 数 (Tokens per second, TPS)**：衡量系统在单位时间内总共生成或处理的 token 数量，反映了系统的整体处理能力。分为输入 TPS 和输出 TPS，有时也报告总 TPS。
        - **每秒请求数 (Requests per second, RPS)**：衡量系统在单位时间内完成的用户请求数量，对于处理大量短交互的系统尤为重要。
    - 延迟 (Latency)：
        - **首个 Token 生成时间 (Time To First Token, TTFT)**：从提交请求到客户端接收到第一个生成 token 所需的时间。
        - **每个输出 Token 时间 (Time Per Output Token, TPOT) / Token 间延迟 (Inter-Token Latency, ITL)**：生成第一个 token 后，生成每个后续 token 所需的平均时间。影响响应其余部分内容流式传输的速度。
        - **总请求延迟 (Total Request Latency)**：从提交请求到接收到最后一个 token 的总时间。
    - **GPU 显存消耗 (GPU Memory Consumption)**：峰值和平均显存使用量。这决定了能够运行多大的模型、可行的批处理大小以及硬件成本。
    - **成本效益 (Cost Efficiency)**：通常以单位成本的性能来衡量，例如每美元的 TFLOPs 或每美元的 TPS。
    - **功耗 (Power Consumption)**：对于大规模部署，由于运营成本和环境因素，功耗日益受到重视。

- **影响基准测试的因素：**

    - **模型**：模型的架构（如 Transformer、MoE）、参数规模、量化级别（如 FP16、INT8、INT4）。
    - **硬件**：GPU 型号（如 A100, H100）、CPU 性能、系统内存大小和带宽、GPU 间互联（如 NVLink）。
    - **批处理大小 (Batch Size)**：并发处理的请求数或序列数。
    - **序列长度**：输入提示的长度和期望生成的输出 token 数量。
    - **框架特定设置**：是否启用 PagedAttention、连续批处理、特定的优化内核、并行策略等。
    - **工作负载特性**：请求到达率、序列长度的分布、请求类型（交互式 vs. 批处理）。

- **比较基准 (综合数据)：**

  由于不同来源的基准测试条件（模型、硬件、输入/输出长度、并发数等）各不相同，直接进行精确的跨报告比较非常困难。本文将综合多个来源的数据，力求呈现一个相对全面的图景。

    - **vLLM vs. TGI**：
        - vLLM 的 PagedAttention 在其早期论文中显示出比标准 HuggingFace Transformers（可视为 TGI 的早期基线）高达 24 倍的吞吐量提升。
        - 在一项针对 Llama-3 70B Q4 的社区讨论中，vLLM 和 TGI 在 100 并发用户下均达到约 600-650 输出 TPS，单请求延迟 TGI (50-70ms) 略优于 vLLM (60-80ms)。TGI 的优势在于内置安全过滤。
        - llama.cpp 团队的基准测试（针对 llama70b 和 mixtral8x7b）显示，vLLM 和 TGI 在每分钟请求数（RPM）和提示处理+Token生成速率（PP+TG/s）方面显著优于 llama.cpp。vLLM 和 TGI 性能相当接近，在 Mixtral 上 TGI 的 PP+TG/s 略高 。
    - **vLLM vs. LMDeploy vs. SGLang**：
        - Clarifai 的一项基准测试（Qwen2-7B, Llama-3.1-8B, Mistral-7B 在 A100 上，输入/输出长度 2048）：
            - **单请求**：SGLang 的 TTFT 最佳。LMDeploy-TurboMind 的吞吐量最高（平均 88.6 tokens/s），比 vLLM 高 8.12%。
            - **100 并发请求**：SGLang 的 TTFT 在 Qwen 和 Llama 模型上表现优异，但在 Mistral 模型上表现不佳，显示出其优化可能对模型架构敏感。LMDeploy-TurboMind 在吞吐量方面领先，比表现最差的框架高出 20% 以上。TGI 在此测试中出现了 OOM 错误。
        - LMDeploy 官方文档声称其 TurboMind 引擎的吞吐量比 vLLM 高达 1.8 倍，其 4-bit 推理性能是 FP16 的 2.4 倍。
        - Cerebrium 的基准测试（Llama 3.1 70B FP8，单 H100，256 输入/512 输出）：
            - **TTFT (批大小)**：vLLM (123ms) 表现最佳，优于 TensorRT-LLM (194ms) 和 SGLang (340ms)。
            - **吞吐量 (批大小)**：SGLang 表现最佳，达到 460 输出 tokens/s。
    - **TensorRT-LLM 性能**：
        - Baseten 的报告显示，使用 TensorRT-LLM 在 A100 上运行 Mixtral 8x7B 可实现低于 200ms 的 TTFT；在 H100 上运行 7B LLM 可获得 3 倍吞吐量提升；运行 SDXL 可降低 40% 延迟并提升 70% 吞吐量。
        - NLPCloud 引用 BentoML 的基准数据称，TensorRT-LLM 在 NVIDIA GPU 上表现优异，A100 单请求延迟低于 50ms，Llama-3 70B Q4 在 100 并发用户下可达约 700 tokens/s。
        - Baseten 的另一项测试表明，在 H100 GPU 上使用 TensorRT-LLM 优化 Mistral 7B (fp16)，相比 A100 可获得 2-3 倍的吞吐量提升，同时延迟持平或更优，这得益于 H100 更高的内存带宽（约 1.64 倍于 A100）和 FP16 Tensor 计算能力（超过 A100 的 3 倍）。
    - **LLM-Inference-Bench 项目**：
        - 这是一个全面的基准测试套件，评估了 LLaMA、Mistral、Qwen 等模型家族（参数规模从 7B 到 70B/72B 不等）在多种 AI 加速器（NVIDIA GPU, AMD GPU, Intel Habana, SambaNova）上使用不同推理框架（vLLM, TensorRT-LLM, llama.cpp, DeepSpeed-MII）的性能。
        - 测试指标包括 TTFT、ITL、吞吐量、功耗、困惑度（Perplexity）等。
        - 主要发现：TensorRT-LLM 在 NVIDIA 平台上通常提供最高的吞吐量。vLLM 具有更广泛的硬件兼容性，但可能功耗较高。Llama-3-8B 和 Mistral-7B 由于架构上的优势（如 Group Query Attention, GQA），在某些配置下表现优于 Llama-2-7B。
    - **NVIDIA GenAI-Perf 工具**：
        - 这是一个客户端 LLM 基准测试工具，适用于符合 OpenAI API 规范的推理服务（例如 NVIDIA NIM，其后端可能使用 TensorRT-LLM 或 vLLM）。
        - 主要测量 TTFT, ITL, TPS, RPS 等指标，有助于用户在自己的硬件上复现和验证 NVIDIA NIM 等部署方案的性能。
    - **CPU 推理性能**：
        - **vLLM CPU**：其 `benchmark_throughput.py` 脚本可用于离线性能测试。例如，在 DeepSeek-R1-Distill-Qwen-1.5B 模型上，50 个提示，输入长度 64，输出长度 128，使用 bfloat16 精度，可达到 42.33 req/s，5418 输出 TPS。
        - **ONNX Runtime**：通常被推荐用于 CPU 推理，因其针对 CPU 的优化。在某些模型上，ONNX Runtime 的 CPU 推理速度可达原生 PyTorch 的 3 倍 。在 CPU 上推荐使用 uint8 量化模型。
        - **llama.cpp**：虽然在同等任务上其性能通常低于为 GPU 优化的框架（如 vLLM, TGI），但 llama.cpp 本身是为 CPU 高效执行而高度优化的，特别是在消费级硬件和边缘设备上表现突出。

# V. LLM 推理的硬件环境

LLM 推理的性能和效率高度依赖于底层硬件。本节将探讨当前用于 LLM 推理的主要硬件平台及其与各推理框架的适配情况。

- **NVIDIA GPU 的主导地位与架构演进：**
    - NVIDIA GPU，特别是其较新的架构如 `Volta, Turing, Ampere, Ada Lovelace 和 Hopper`，是当前高性能 LLM 推理领域的事实标准硬件，得到了绝大多数主流推理框架的深度支持和优化。
    - `TensorRT-LLM` 作为 NVIDIA 官方的推理库，专为 NVIDIA GPU 设计，能够充分利用其最新硬件特性，例如 Hopper 架构上的 FP8 计算单元 。
    - H100 GPU 相比 A100 GPU，在 LLM 推理方面展现出显著的性能提升（通常为 2-3 倍吞吐量），这主要归功于其更高的内存带宽和更强的张量计算能力。
- **AMD GPU (ROCm) 的崛起与支持：**
    - 随着 AMD ROCm 软件生态的不断成熟，越来越多的 LLM 推理框架开始提供对 AMD GPU 的支持。
    - vLLM 支持通过 ROCm 在 AMD GPU 上运行，并有相关性能报告（如 MI300X）。
    - SGLang 也宣布了对 AMD GPU 的支持 。
    - Text Generation Inference (TGI) 同样支持 AMD GPU，并提供了相应的教程。
    - DeepSpeed-Inference 也支持 AMD 的 MI100 和 MI200 等加速卡。
    - LLM-Inference-Bench 等基准测试项目已将 AMD GPU 纳入其评估范围。
- **专用加速器 (Specialized Accelerators)：**
    - Intel Gaudi HPU (Habana Processing Units)：
        - Intel Gaudi 系列（Gaudi1, Gaudi2, Gaudi3）作为专为 AI 设计的加速器，正获得越来越多框架的青睐。TGI 已实现对 Gaudi 的原生集成，支持多卡推理、视觉语言模型，并通过 Intel Neural Compressor (INC) 支持 FP8 精度。
        - vLLM 通过 Intel Extension for PyTorch，也支持在 Intel GPU（如 Arc, Data Center Max 系列）以及 Gaudi 处理器上运行，并利用了 vLLM v1 引擎的特性，如 chunked_prefill。
        - DeepSpeed 同样支持在 Gaudi 上进行推理。
        - Intel Neural Compressor 本身支持 Gaudi 加速器和 ONNX Runtime，为在 Gaudi 上运行优化后的 ONNX 模型提供了通路。
        - PyTorch 和 Hugging Face 等主流框架也已支持 Gaudi 平台。
    - FPGA 解决方案：
        - 基于 FPGA 的 LLM 推理方案，如 FlightLLM 项目，展示了在特定场景下（如 LLaMA2-7B 单批次推理）相比传统 GPU（如 NVIDIA V100S）具有更高能效比和成本效益的潜力，甚至在特定 FPGA（如 Xilinx Versal VHK158）上实现了比 A100 更高的吞吐量。
        - 然而，FPGA 方案目前面临的主要挑战包括软件生态不成熟、模型支持有限、开发工具链复杂以及高端 FPGA 本身成本较高等问题。其易用性和生态成熟度远不及 GPU 解决方案。
- **CPU 推理：能力、局限与应用场景：**
    - **vLLM**：已扩展支持多种 CPU 架构（x86, ARM64, Apple Silicon, s390x），并提供了一些 CPU 特有的优化功能，如张量并行和部分量化方法。但通常需要从源码编译，且性能调优（如内存分配器选择、线程绑定）对用户有一定要求。
    - **ONNX Runtime**：通常被推荐用于 CPU 推理，因其广泛的硬件支持和针对 CPU 的优化执行提供程序。在某些情况下，ONNX Runtime 在 CPU 上的性能可数倍于原生的 PyTorch 实现。
    - **SGLang**：虽然其架构中包含“零开销 CPU 调度器”，但其性能优化的核心仍在 GPU。关于纯 CPU 推理的详细性能数据和特定设置指南在现有资料中较为缺乏。
    - **FasterTransformer**：其 GPU 版本主要面向 NVIDIA GPU。针对 x86 CPU 的优化由 Intel 主导的独立项目 xFasterTransformer  负责。
    - **DeepSpeed**：虽然提供了基础的 CPU 使用建议，但其在 CPU 上的应用更多是作为异构计算中加速器显存不足时的卸载目标，或用于运行非常小的模型。
    - **总体而言**：对于大型 LLM 的高吞吐量、低延迟服务，CPU 推理在性能上通常无法与 GPU 抗衡。但在模型较小、对成本极度敏感、特定边缘计算场景或开发测试等情况下，CPU 推理仍有一席之地。RAM 的速度和带宽对 CPU 推理性能影响显著。
- **框架利用的硬件特定优化：**
    - NVIDIA GPU 的 Tensor Cores (TensorRT-LLM, FasterTransformer)。
    - NVIDIA Hopper 架构的 FP8 计算单元 (TensorRT-LLM)。
    - Intel GPU/Gaudi 的 Intel Extension for PyTorch (vLLM, TGI)。
    - AMD GPU 的 ROCm 软件栈 (vLLM, TGI)。
    - x86 CPU 的 AVX512 指令集 (vLLM)。

尽管 `NVIDIA GPU` 目前在高性能 LLM 推理领域占据主导地位，但市场对硬件多样化、成本降低和特定场景专用解决方案的需求日益增长。这推动了对 `AMD GPU、Intel Gaudi、FPGA` 以及更优化的 CPU 推理方案的支持和发展。未来，LLM 推理的硬件环境可能会更加异构化。这种趋势为用户提供了更多选择，但也可能增加硬件选型和软件优化的复杂性。

CPU 推理的图景也在分化：一方面是针对边缘设备和客户端的高度优化的小型模型推理（通常借助 ONNX Runtime 或类 llama.cpp 引擎）；另一方面是在异构计算设置中，CPU 用于卸载大型模型的部分计算或存储（如 PowerInfer 106 或 DeepSpeed 的异构内存技术 ）。对于大规模、高吞吐的 LLM 服务，纯 CPU 推理仍然是一个小众选择。

FPGA 在 LLM 推理方面的应用，虽然在能效和成本效益上展现出潜力，但目前受限于其软件生态、模型支持和开发门槛。其未来的成功将取决于能否弥合与 GPU 解决方案在软件和易用性上的差距。除非 FPGA 对 LLM 的编程和集成变得像 GPU 一样便捷，否则其应用可能仍局限于那些能够承担定制硬件开发成本的特定大批量场景。

# VI. 部署策略与生态系统集成

将 LLM 推理框架有效地部署到生产环境，并与现有 MLOps 生态系统集成，是实现其价值的关键。本节将探讨部署复杂度、容器化与编排、以及与各类服务平台和模型中心的集成情况。

- **部署复杂度：**
    - **安装便捷性**：多数主流框架如 `vLLM, OpenLLM, LMDeploy` 等支持通过 `pip` 命令直接安装。Docker 容器简了安装方式，TGI, vLLM, TensorRT-LLM (通过 NGC 容器), LMDeploy 等均提供或推荐使用 Docker 部署。
    - **配置**：不同框架的配置复杂度各异。`TensorRT-LLM` 涉及一个将模型编译为优化引擎的步骤，这可能需要针对特定硬件和模型进行调整。TGI 主要通过环境变量和命令行参数进行配置。vLLM 也大量使用命令行参数来控制其行为，如模型路径、并行度、KV 缓存策略等。
    - **管理**：与 KServe 或 BentoML 等模型服务平台集成的框架，可以利用这些平台提供的模型管理、版本控制、自动扩缩容等能力。
- **容器化 (Docker) 与编排 (Kubernetes, KServe)：**
    - **Docker**：已成为打包 LLM 推理服务器及其依赖的标准方式，确保了环境的可移植性和一致性 14。
    - **Kubernetes**：作为容器编排的事实标准，广泛用于部署、管理和扩展包括 LLM 服务在内的容器化应用。
    - KServe：是一个构建在 Kubernetes 之上的标准模型推理平台，旨在为多种机器学习框架提供统一的服务接口和无服务器推理体验。
        - **vLLM**：可以通过 KServe 的 Hugging Face Serving Runtime 进行部署，该运行时通常默认使用 vLLM 作为后端以提升性能 。
        - **TGI**：同样可以部署在 KServe 上，常作为 Hugging Face 模型的一种服务运行时选项，有时 vLLM 会作为其备选或补充后端 。
        - **TensorRT-LLM**：由于 Triton Inference Server 支持 KServe API，而 TensorRT-LLM 引擎通常通过 Triton 部署，因此可以间接获得 KServe 的支持。NVIDIA 的 NIM 微服务也可以部署在 KServe 之上。
        - **FasterTransformer**：Triton 的一个后端可以是 FasterTransformer，而 Triton 支持 KServe。另外，TorchServe（可以集成 FasterTransformer）也支持 KServe。通用的 ONNX 模型也可以通过 KServe 服务。
        - **LMDeploy**：在核心文档摘要中未直接提及与 KServe 的官方深度集成。一个 GitHub issue 提到了在 Kubernetes 环境中部署的场景。KServe 本身作为一个通用平台，理论上可以集成自定义的 LMDeploy 服务。
- **与服务平台 (BentoML, Triton Inference Server) 和模型中心 (Hugging Face) 的集成：**
    - BentoML：
        - **vLLM**：与 BentoML 有良好的集成，可以将 vLLM 作为后端，打包成 Bento 服务，并暴露 OpenAI 兼容的 API 端点。
        - **OpenLLM**：本身就是基于 BentoML 构建的，旨在提供一种便捷的方式来服务化开源 LLM。
        - **通用性**：BentoML 是一个用于构建、分发和扩展 AI 应用的通用框架，支持 LLM、扩散模型等多种 AI 模型。
    - NVIDIA Triton Inference Server：
        - **TensorRT-LLM**：通过 Triton 的 TensorRT-LLM 后端进行部署是其主要的生产化服务方式 。
        - **FasterTransformer**：拥有专门的 Triton 后端。
        - **vLLM**：可以与 Triton 集成部署。
        - **通用性**：Triton 支持多种后端，包括 Python, ONNX, TensorRT, PyTorch 等，是一个灵活的多模型服务平台。
    - Hugging Face Hub：
        - 几乎所有主流的 LLM 推理框架（`vLLM, TGI, TensorRT-LLM, FasterTransformer, LMDeploy, SGLang, OpenLLM`）都支持从 `Hugging Face Hub` 加载模型权重和配置文件。
        - TGI 作为 Hugging Face 的官方产品，与其生态系统集成最为紧密。
- **ONNX 在互操作性中的作用：**
    - ONNX (Open Neural Network Exchange) 定义了一种通用的模型表示格式和算子集，旨在实现不同深度学习框架（如 PyTorch, TensorFlow）与推理运行时之间的模型可移植性 。
    - ONNX Runtime (ORT) 可以执行这些 ONNX 格式的模型，并通常能利用硬件加速特性（通过其执行提供程序机制)。
    - TensorRT 能够解析 ONNX 模型，并将其编译为高度优化的 TensorRT 引擎。
    - FasterTransformer 支持 ONNX 模型的转换。
    - 对于 `vLLM, TGI, LMDeploy, SGLang` 等主要基于 PyTorch 进行优化的框架而言，它们的核心推理路径通常直接操作原生的 PyTorch 模型。虽然这些模型理论上可以先转换为 ONNX 格式再由 ONNX Runtime 执行，但这并非它们发挥其特有优化（如 PagedAttention、自定义内核）的主要方式。现有资料中并未显示这些框架将 ONNX 模型作为其核心引擎的直接输入格式。LMDeploy 作为 MMDeploy 的一部分，对 ONNX 自定义算子有支持，但其高性能 LLM 推理依赖于自有的 TurboMind 和 PyTorch 引擎。

LLM 部署生态正朝着容器化（Docker+ Kubernetes）的方向发展，KServe 作为 Kubernetes 上的一个标准化接口，为无服务器推理提供了便利。这种标准化趋势简化了 LLM 的 MLOps 流程。

尽管 ONNX 提供了模型互操作性的承诺，但高性能 LLM 推理往往依赖于特定框架的深度优化（如 PagedAttention、自定义CUDA内核），这些优化可能难以完全或高效地通过通用的 ONNX 图表示来传递。因此，许多专用推理引擎（如 vLLM, TGI, SGLang）为了达到峰值性能，更倾向于直接处理原生的模型格式（如 PyTorch checkpoints），而不是依赖 ONNX 作为其核心执行路径的中间表示。ONNX 可能在模型导入阶段被某些工具（如 TensorRT-LLM）使用，但最终生成的优化引擎是高度特化的。

另一个需要关注的趋势是，越来越多的自托管 LLM 解决方案（如 `OpenLLM, vLLM server, KServe, LMDeploy`）开始提供`兼容 OpenAI API`特性。这正在催生一个子生态系统，允许企业和开发者利用现有的大量与 OpenAI API 交互的工具和开发经验，同时保持对自身模型和基础设施的控制权。这种兼容性极大地降低了将自托管开源 LLM 集成到现有工作流中的门槛，加速了自托管方案的采纳。

# VII. 不同场景下的框架选型实践建议

选择合适的 LLM 推理框架对于在特定业务场景下实现最佳性能、成本效益和可管理性至关重要。以下将针对不同部署规模和业务需求提供选型建议。

- **单卡部署：优化资源受限环境**

    - **vLLM** 的 PagedAttention 和 **SGLang** 的 RadixAttention 等内存管理技术能有效减少显存碎片，提高利用率，从而在单张 GPU 卡上容纳更大的模型或处理更大的批次。
    - **量化**是必不可少的手段。选择支持 INT8、INT4 (如 GPTQ, AWQ) 等低精度量化且量化后精度损失可控的框架。多数主流框架（如 vLLM, LMDeploy, TensorRT-LLM）都提供了良好的量化支持。
    - 对于资源极其有限的场景（如边缘设备），可以考虑 **CPU 推理**。**ONNX Runtime** 和 **llama.cpp** 是常见的选择，特别是对于较小规模的模型。vLLM 对 CPU 推理的支持也在逐步完善。

- **规模化部署：多卡与多节点推理策略**

    - **张量并行 (Tensor Parallelism, TP)**：将模型层的权重和计算分割到同一节点内的多个 GPU 上。适用于模型层过大或希望通过并行计算加速层内运算的场景。`vLLM, TensorRT-LLM, FasterTransformer, LMDeploy, SGLang` 均支持 TP。
    - **流水线并行 (Pipeline Parallelism, PP)**：将模型的不同层分配到不同的 GPU 或节点上，形成处理流水线。适用于模型深度过大，即使使用 TP 也难以在单节点内容纳的场景。`TensorRT-LLM, FasterTransformer, vLLM (多节点) `支持 PP。
    - **专家并行 (Expert Parallelism, EP)**：专门针对混合专家 (MoE) 模型，将不同的专家模块分布到不同设备上。TensorRT-LLM 和 SGLang (通过其 PD Disaggregation 支持大规模 EP) 对此有良好支持。
    - 框架选择：
        - **TensorRT-LLM**：通过 MPI 提供强大的多节点支持，适合构建大规模、高性能的 NVIDIA GPU 集群。
        - **vLLM**：也支持多节点部署，结合其高效的内存管理，适合构建可扩展的服务。
        - **LMDeploy**：其“分布式服务器”概念旨在简化多机多卡的模型服务部署。
        - **SGLang**：其 PD Disaggregation 架构天然支持跨节点的预填充和解码分离，适用于大规模分布式推理。

- **基于业务场景的推荐：**

    - 交互式/低延迟应用 (如聊天机器人、实时助手)：
        - **核心需求**：极低的 TTFT 和 TPOT (ITL)。
        - 推荐框架：
            - **TensorRT-LLM**：在 NVIDIA 硬件上通常能提供非常低的单请求或小批量请求延迟。
            - **vLLM**：在某些基准测试中也显示出良好的 TTFT 表现，其连续批处理有助于在高并发下维持较低的平均延迟。
            - **SGLang**：可以通过调整配置来优化低延迟场景，其 RadixAttention 和高效调度器有潜力提供快速响应。
        - **关键技术**：连续批处理、高效的 KV 缓存管理、投机解码（如果适用且能带来收益）。
    - 高吞吐量批处理应用 (如离线文本生成、文档摘要)：
        - **核心需求**：最大化 TPS 和 RPS。
        - 推荐框架：
            - **vLLM**：其 PagedAttention 和连续批处理设计使其在高吞吐场景下表现出色。
            - **TensorRT-LLM**：配合 Triton Inference Server，能够处理非常高的并发量。
            - **LMDeploy-TurboMind**：声称比 vLLM 有更高的吞吐量，尤其在量化模型上。
            - **SGLang**：在一些基准测试中（特别是针对 70B 模型和高并发）展现出领先的吞吐量。
        - **关键技术**：PagedAttention/RadixAttention、连续批处理、优化的 CUDA 内核、有效的并行策略。
    - 特定模型架构支持：
        - **混合专家模型 (MoE)**：优先考虑 **TensorRT-LLM** 和 **SGLang**，它们对专家并行有专门的支持。
        - **视觉语言模型 (VLM)**：**TGI** (在 Gaudi 上) , **SGLang**, **LMDeploy** 均提供了对 VLM 的支持或专用推理管线。
    - 成本敏感型部署：
        - **核心需求**：在满足基本性能要求的前提下，最大化单位价格的性能。
        - 策略：
            - 选择支持强力**量化**（如 INT4）且精度损失可控的框架，以降低硬件需求或在同等硬件上运行更大模型/批次（例如 LMDeploy, vLLM）。
            - 考虑使用 **AMD GPU** 或 **Intel Gaudi** 等替代硬件，如果所选框架对其有良好支持且这些硬件具有更好的性价比（例如 TGI on Gaudi, vLLM on AMD）。
            - 对于极小模型或非性能关键任务，**CPU 推理** 可能是成本最低的选择。
            - 优先选择**开源框架**（`vLLM, TGI, SGLang, LMDeploy`），避免商业软件的授权费用。
    - 易用性与生态集成：
        - **TGI**：对于深度整合 Hugging Face 生态的用户，TGI 提供了最便捷的部署体验。
        - **OpenLLM / BentoML**：如果目标是快速搭建具有 OpenAI 兼容 API 的服务，这两个框架提供了很好的抽象和便利性。
        - **vLLM**：在性能和易用性之间取得了较好的平衡，拥有活跃的社区和广泛的模型支持，是许多场景下的通用优选。

  **表 ：LLM 推理框架选型决策矩阵参考**

| **场景/需求**                       | **关键考量点**                                     | **推荐框架特性**                                             | **示例框架**                                        |
| ----------------------------------- | -------------------------------------------------- | ------------------------------------------------------------ | --------------------------------------------------- |
| **单卡部署 (资源受限)**             | 显存大小、计算能力、成本                           | PagedAttention/RadixAttention, 高效量化 (INT4/AWQ/GPTQ), CPU 推理支持 (备选) | vLLM, SGLang, LMDeploy, ONNX Runtime (CPU)          |
| **多卡单节点 (性能扩展)**           | NVLink/PCIe 带宽、节点内 GPU 数量                  | 张量并行 (TP), 高效节点内通信                                | TensorRT-LLM, vLLM, SGLang, LMDeploy                |
| **多卡多节点 (超大规模模型/服务)**  | 网络带宽与延迟、节点间同步开销                     | 流水线并行 (PP), 专家并行 (EP), 优化的分布式通信 (MPI), PD Disaggregation | TensorRT-LLM, SGLang, vLLM (Ray)                    |
| **延迟关键型应用 (如实时聊天)**     | TTFT, TPOT, 单请求/小批量性能                      | 低延迟调度, 投机解码, 优化内核, 快速 KV 缓存访问             | TensorRT-LLM, vLLM, SGLang                          |
| **吞吐量关键型应用 (如离线批处理)** | TPS, RPS, 大批量处理效率                           | 连续批处理, PagedAttention/RadixAttention, 高效并行, 充分利用硬件计算能力 | vLLM, SGLang, TensorRT-LLM, LMDeploy                |
| **多任务/多 LoRA 服务**             | LoRA 适配器管理, 基础模型共享效率, 动态加载/切换   | S-LoRA, 动态 LoRA 加载, 高效 LoRA 内核, 多任务量化 (MLGPTQ)  | vLLM, TensorRT-LLM, LMDeploy (PyTorch), TGI         |
| **成本优化 (性价比优先)**           | 硬件成本, 运营成本 (功耗), 开源许可                | 强量化支持, 对非 NVIDIA 硬件 (AMD/Gaudi/CPU) 的良好支持, 资源利用率高 | vLLM, TGI (on Gaudi/AMD), LMDeploy, SGLang (on AMD) |
| **追求极致性能 (NVIDIA 生态)**      | 充分发挥 NVIDIA 最新硬件特性 (如 Hopper FP8)       | 深度硬件绑定优化, 模型编译, 最新 CUDA 特性支持               | TensorRT-LLM                                        |
| **需要广泛硬件兼容性**              | 支持多种 GPU 厂商, CPU, 其他加速器                 | 跨平台设计, 模块化后端                                       | vLLM, ONNX Runtime                                  |
| **易用性与快速原型验证**            | 安装便捷, 配置简单, 与 Hugging Face 等生态集成度高 | 简洁 API, 良好文档, 社区活跃, 预置 Docker 镜像, OpenAI 兼容接口 | TGI, OpenLLM, vLLM                                  |
| **复杂 Agentic 系统或结构化输出**   | 对多步推理、工具调用、受控生成的支持               | 前端编程语言 (DSL), 结构化输出约束 (如 xGrammar), 高效的跨调用状态管理 (RadixAttention) | SGLang                                              |

# VIII. LLM 推理的前沿技术与未来展望

LLM 推理领域的技术创新步伐持续加快，一系列前沿技术正在涌现，旨在进一步提升效率、降低成本、扩展应用场景。

- **A. 投机解码 (Speculative Decoding) 的进展**
    - **核心思想**：投机解码通过使用一个计算开销较小、速度更快的`草稿模型`（draft model）来预测未来数个 token，然后由原始的、更大更精确的`目标模型`（target model）并行地对这些草稿 token进行验证。如果草稿 token 被验证通过，则可以一次性接受多个 token，从而加速整个解码过程，尤其是在内存带宽受限的场景下能够有效降低每个 token 的平均生成延迟。
    - 框架支持现状：
        - **vLLM**：已支持多种投机解码策略，包括使用独立的草稿模型、基于提示中的 N-gram 匹配生成草稿，以及使用 MLP 推测器（MLP speculators）。在 AMD MI300X GPU 上的测试显示，启用投机解码的 vLLM 最高可获得 2.31 倍的加速。
        - **Text Generation Inference (TGI)**：在其功能列表中也包含了对投机解码的支持。
        - **TensorRT-LLM**：支持包括 `Medusa、ReDrafter、Lookahead Decoding、Eagle` 在内的多种高级投机解码算法。Baseten 等平台也基于 TensorRT-LLM 提供了投机解码的引擎构建服务。
        - **SGLang**：在其核心特性中也列出了对投机解码的支持。
        - **LM Studio (llama.cpp & MLX 引擎)**：在其较新版本中也集成了基于草稿模型和主模型的投机解码功能。
    - **关键技术点**：成功的投机解码依赖于高效的草稿选择（drafter selection）和验证策略（verification strategies），如贪心验证、基于采样概率的验证、以及更复杂的 Token Tree 验证等。
    - **影响与挑战**：投机解码的实际性能增益取决于草稿模型的质量、草稿 token 的接受率、目标模型与草稿模型之间的行为一致性，以及硬件平台的特性。在草稿质量不高或接受率较低时，投机解码甚至可能引入额外开销导致性能下降。
- **B. “训推一体” (Serving-as-Training) 范式**
    - **核心理念**：打破传统训练和推理在资源和软件栈上的壁垒，寻求统一或更紧密协同的系统架构。这有望提高资源利用效率，并使模型的持续学习、在线微调和与真实推理反馈的闭环迭代更加流畅和高效。
    - **现状与挑战**：目前，多数 LLM 推理框架仍主要专注于优化推理（前向传播）过程。而“训推一体”则要求框架具备灵活的资源管理能力，能够适应训练（包含反向传播和参数更新）和推理两种截然不同的工作负载。虽然像联邦学习 这样的技术体现了分布式模型更新的思想，但真正意义上在核心引擎层面实现高效的“训推一体”仍面临诸多挑战。
- **C. 异构计算 (Heterogeneous Computing) 的深入利用**
    - **核心理念**：通过组合使用不同类型的计算硬件（如 `CPU、GPU、TPU、NPU` 及其他专用 AI 加速器），并为推理流程中的不同计算任务或不同类型的请求匹配最适合的硬件，以达到性能和成本的最优化。
    - 典型方案：
        - **PowerInfer**：一种创新的架构，它利用 LLM 推理过程中神经元激活的稀疏性和局部性特性，将频繁激活的“热”神经元加载到 GPU 上高速处理，而将大部分不常激活的“冷”神经元放在 CPU 上处理，从而显著降低对 GPU 显存的需求，使得在消费级 GPU 上运行大型模型成为可能。
        - **DeepSpeed-Inference**：其设计支持异构内存技术，允许模型参数和激活值分布在 GPU HBM、CPU RAM 甚至 NVMe SSD 等不同层级的存储介质上，以支持超出单 GPU 显存容量的超大模型推理。
        - **Mélange 框架**：一个自适应扩展框架，它根据请求大小、请求速率和服务等级目标（SLO）动态地组合使用不同类型的 GPU（如高端 GPU 处理低延迟请求，低端 GPU 处理成本敏感或非实时请求），据称可节省高达 77% 的成本。
    - **挑战**：异构计算的主要挑战在于复杂的工作负载分配、跨不同硬件的数据迁移开销、以及需要精密的软件编排和调度系统。
- **D. 动态高效的 LoRA 适配器服务**
    - **核心理念**：在单个共享的基础 LLM 之上，高效地服务多个 LoRA (Low-Rank Adaptation) 适配器。每个 LoRA 适配器针对特定任务或用户进行了微调，仅包含少量参数。这种方式允许系统在不同任务间快速切换或为不同用户提供个性化服务，而无需加载和维护多个完整的 LLM 副本，从而大幅节省显存和计算资源。
    - 框架支持现状：
        - **vLLM**：支持在服务启动时加载多个 LoRA 模块，并能通过 API 接口或插件机制在运行时动态加载和卸载 LoRA 适配器，每个请求可以指定使用哪个 LoRA，开销极小。
        - **Text Generation Inference (TGI)**：允许在服务器启动时加载多个 LoRA 适配器，并在推理请求中指定使用哪个适配器。
        - **TensorRT-LLM**：原生支持使用单个（通常是量化后的）基础模型检查点来服务多个 LoRA 适配器。支持并发处理来自不同 LoRA 的请求，或单个请求使用多个 LoRA 进行批处理生成（例如，一个提示同时生成多种语言的响应）。
        - **LMDeploy (PyTorch 引擎)**：通过 S-LoRA 技术支持高效服务多个 LoRA 适配器，采用分页和按需交换策略。
    - **关键技术**：包括 Punica、S-LoRA 等针对 LoRA 服务优化的内核和调度算法，以及 LoRA-Inlaid 提出的多任务量化（MLGPTQ）和多任务调度策略，旨在进一步提升多 LoRA 场景下的效率和灵活性。
    - **优势**：模块化、可扩展性强、成本和资源效率高、能够快速实现模型专业化和个性化服务。
- **E. 针对复杂智能体系统 (Agentic Systems) 的推理优化**
    - **面临的挑战**：基于 LLM 的智能体系统（如使用 LangChain, AutoGen 等框架构建的应用）通常涉及多次 LLM 调用、与外部工具的交互、以及复杂的状态管理和决策流程。这对此类应用的推理优化提出了新的挑战，远超单次请求-响应模式的文本生成。
    - 智能体框架：
        - **AutoGen**：专注于多智能体对话编排，采用事件驱动架构，支持分布式智能体运行时。它依赖底层的模型客户端（如 OpenAI API 或本地 LLM 服务）进行实际的 LLM 推理。
        - **LangChain**：强调组件的可组合性，通过链（Chains）和工具（Tools）构建应用。LangGraph 模块用于对多智能体交互进行建模。
    - 推理优化需求：
        - 高效缓存 LLM 调用结果和中间状态（AutoGen 提供此类缓存机制）。
        - 优化处理大量、短小且相互依赖的 LLM 调用序列。
        - 高效集成工具执行与 LLM 推理流程。
        - 在可能的情况下，对来自多个智能体或多个执行步骤的 LLM 调用进行批处理。
        - **SGLang** 的前端 DSL 和后端优化（如 RadixAttention）使其非常适合处理这类复杂的、程序化的 LLM 工作流。
- **F. 预填充-解码 (Prefill-Decode, PD) 分离**
    - **核心理念**：将 LLM 推理过程中的预填充阶段（处理输入提示，计算初始 KV 缓存）和解码阶段（自回归生成后续 token）明确分离，并可能将它们调度到不同的硬件资源上或采用不同的优化策略，以分别优化这两个计算特性显著不同的阶段。
    - **理论依据**：预填充阶段通常对于长提示是计算密集型的（需要并行处理所有输入 token），而解码阶段则更受内存带宽限制且本质上是序列化的（逐个 token 生成）。
    - **SGLang 的实现**：SGLang 已经实现了 PD 分离架构，通过在预填充服务器和解码服务器之间交错执行，并结合大规模专家并行（DeepEP），显著提升了吞吐量，例如在其报告的 DeepSeek 推理系统复现中，单节点（8xH100）实现了每秒 52.3k 输入 token 和 22.3k 输出 token 的吞吐量（针对 2000 token 输入长度）。

LLM 推理的前沿正从单纯追求原始 token 生成速度，转向支持更复杂、动态和个性化的 AI 应用。这要求推理框架不仅要快，还要在资源管理、任务编排和功能灵活性方面更加智能和强大。例如，投机解码优化了生成循环本身，动态 LoRA 服务实现了共享基础模型上的个性化和多任务处理，异构计算和 PD 分离则在系统层面优化资源利用。智能体编排则处理涉及多次 LLM 调用和工具使用的复杂工作流。这些趋势共同指向了对能够支持更高级应用场景的推理基础设施的需求。

同时，技术也在趋同：在一个框架中被验证有效的特性（如 vLLM 的 PagedAttention）会被其他框架借鉴或启发类似的机制（如 SGLang 的 Token Attention，LMDeploy 的分页调度器 ）。像投机解码和 LoRA 支持这样的高级功能，正逐渐成为有竞争力的推理框架的标准配置。这种开源驱动的快速迭代和思想传播，使得整个生态系统受益，用户能够获得功能更强大、效率更高的工具，但也使得框架间的“独特功能”比较更具时效性。

**“训推一体” **的概念虽然在核心 LLM 推理引擎层面尚处于早期，但它反映了行业对更敏捷 AI 生命周期管理的广泛期望，即模型能够以最小的训练与部署环境摩擦持续更新和改进。这可能会驱动未来推理框架在数据和资源管理方面向更统一的设计演进。

# IX. 总结与建议

大语言模型推理框架是释放 LLM 潜能、将其应用于实际生产的关键技术。本次调研对当前主流的 LLM 推理框架，包括 `vLLM、Text Generation Inference (TGI)、NVIDIA TensorRT-LLM、NVIDIA FasterTransformer、LMDeploy 以及 SGLang` 等，进行了系统性的分析。

**核心结论：**

1. **性能优化是核心驱动力**：所有主流框架都致力于通过各种技术手段（如高效 KV 缓存管理 - PagedAttention/RadixAttention，连续批处理，优化的 CUDA 内核，并行计算，量化等）来提升推理吞吐量、降低延迟并优化显存利用。
2. **框架各有侧重，不存在“银弹”**：
    - **vLLM** 以其 PagedAttention 在内存管理和高吞吐方面表现突出，且社区活跃，硬件支持广泛。
    - **TGI** 凭借与 Hugging Face 生态的紧密集成和对多种新兴硬件（如 Gaudi）的快速支持，在易用性和模型覆盖面上有优势。
    - **TensorRT-LLM** 作为 NVIDIA 的官方解决方案，在 NVIDIA GPU 上能提供极致的优化性能，特别适合追求峰值表现且技术栈与 NVIDIA 深度绑定的用户。
    - **FasterTransformer** 曾是底层优化的先驱，其许多技术已被更上层框架吸收，目前直接使用可能较少。
    - **LMDeploy** 以其双引擎（TurboMind C++/CUDA + PyTorch Python）设计，试图平衡性能与开发灵活性，并在量化和多 LoRA 支持方面有特色。
    - **SGLang** 通过前端语言与后端运行时的协同设计，在处理复杂 LLM 应用（如 Agent、结构化输出）和实现高吞吐方面展现出强大潜力。
3. **硬件生态日益多样化**：虽然 NVIDIA GPU 仍是主流，但对 AMD GPU、Intel Gaudi 等替代硬件的支持正在快速增长。CPU 推理也在特定场景（小型模型、边缘计算、成本极度敏感）下占有一席之地。异构计算是未来的重要趋势。
4. **部署与生态集成走向成熟**：容器化（Docker）、Kubernetes 编排以及 KServe 等标准化平台简化了 LLM 的部署和管理。与 Hugging Face Hub 的集成已成为标配。OpenAI 兼容 API 接口也成为许多自托管方案的共同选择。
5. **前沿技术持续演进**：投机解码、动态 LoRA 服务、预填充-解码分离等技术正在被广泛采纳和优化。针对 Agentic 系统和“训推一体”的优化是未来的重要方向。

**实践建议：**

1. **明确业务需求与性能指标**：在选型前，务必清晰定义应用场景（交互式 vs. 批处理）、核心性能指标（TTFT, TPOT, TPS, RPS）、可接受的延迟、预期的并发规模、模型大小与类型（如是否为 MoE, VLM）以及预算限制。
2. **评估硬件环境与兼容性**：考虑现有的和计划采购的硬件。若深度绑定 NVIDIA 生态且追求极致性能，TensorRT-LLM 是强力候选。若需要更广泛的硬件选择（AMD, Intel Gaudi, CPU），或希望避免供应商锁定，vLLM, TGI, SGLang 等提供了更多选项。
3. **关注内存效率与量化支持**：对于显存敏感的大模型部署，PagedAttention (vLLM)、RadixAttention (SGLang) 等技术至关重要。同时，考察框架对 INT8, INT4, FP8 等量化方案的支持程度及其对模型精度的影响。
4. **考虑部署复杂度与运维成本**：选择与团队技术栈和运维能力相匹配的框架。与 KServe, BentoML 等平台集成的框架可以降低运维复杂度。Docker 化部署是推荐的最佳实践。
5. **重视生态系统与社区支持**：活跃的开源社区（如 vLLM, SGLang）意味着更快的迭代、更及时的 Bug 修复和更丰富的学习资源。与 Hugging Face 等主流模型库的兼容性也是重要考量。
6. 针对特定场景进行细致选型：
    - **低延迟交互**：优先考虑 `TensorRT-LLM, vLLM, SGLang`，并关注其投机解码和小批量处理性能。
    - **高吞吐批处理**：`vLLM, SGLang, TensorRT-LLM, LMDeploy (TurboMind)` 在大批量处理上各有优势。
    - **多任务/多 LoRA 服务**：`vLLM, TensorRT-LLM, LMDeploy (PyTorch Engine), TGI` 提供了不同程度的多 LoRA 支持。
    - **复杂应用逻辑 (Agents, 结构化输出)**：SGLang 的前端语言设计为此类应用提供了独特优势。
7. **进行实际基准测试**：鉴于公开基准测试的条件差异，强烈建议在自身的目标硬件和典型工作负载下对候选框架进行实际的性能评估。利用如` LLM-Inference-Bench, GenAI-Perf` 等工具或方法进行对比。
8. **关注新兴技术趋势**：保持对投机解码、PD 分离、异构计算、训推一体等前沿技术的关注，它们可能在未来显著改变 LLM 推理的格局，并为新的优化机会和应用场景打开大门。

总之，LLM 推理框架的选择是一个多维度权衡的过程。通过深入理解各框架的核心特性、性能表现，并结合具体的业务需求和技术储备，可以做出最适合的决策，从而高效、经济地赋能大语言模型在各类应用中的落地。



## 参考链接

1.  [Red Hat Blog on vLLM](https://www.redhat.com/en/blog/meet-vllm-faster-more-efficient-llm-inference-and-serving)
2.  [Clarifai Blog Comparing vLLM, LMDeploy, and SGLang](https://www.clarifai.com/blog/comparing-vllm-lmdeploy-and-sglang)
3.  [Hugging Face Blog on Text Generation Inference (TGI) at Scale](https://huggingface.co/blog/martinigoyanes/llm-inference-at-scale-with-tgi)
4.  [Hugging Face Blog on TGI Benchmarking](https://huggingface.co/blog/tgi-benchmarking)
5.  [Baseten Blog on High Performance ML Inference with NVIDIA TensorRT-LLM](https://www.baseten.co/blog/high-performance-ml-inference-with-nvidia-tensorrt/)
6.  [Run:ai Benchmarking Study: Serving Large Language Models (PDF)](https://pages.run.ai/hubfs/PDFs/Serving-Large-Language-Models-Run-ai-Benchmarking-Study.pdf)
7.  [NVIDIA Triton Inference Server Docs for TensorRT-LLM Backend](https://docs.nvidia.com/deeplearning/triton-inference-server/user-guide/docs/tensorrtllm_backend/README.html)
8.  [AWS Blog on Deploying Large Models with FasterTransformer on Amazon SageMaker](https://aws.amazon.com/blogs/machine-learning/deploy-large-models-at-high-performance-using-fastertransformer-on-amazon-sagemaker/)
9.  [Milvus Blog on Frameworks for LLM Training and Inference](https://milvus.io/ai-quick-reference/what-frameworks-support-llm-training-and-inference)
10.  [OpenLLM PyPI Page (v0.1.5)](https://pypi.org/project/openllm/0.1.5/)
11.  [DeepSpeed GitHub Repository](https://github.com/deepspeedai/DeepSpeed)
12.  [Habana Docs for DeepSpeed Inference on Gaudi](https://docs.habana.ai/en/latest/PyTorch/DeepSpeed/Inference_Using_DeepSpeed.html)
13.  [PyTorch Blog on Accelerating LLM Inference (TorchAO, SGLang, GemLite)](https://pytorch.org/blog/accelerating-llm-inference/)
14.  [ONNX Runtime Inference Page](https://onnxruntime.ai/inference)
15.  [ONNX Runtime for Generative AI Page](https://onnxruntime.ai/generative-ai)
16.  [NVIDIA Developer Blog on Benchmarking LLM Inference with GenAI-Perf and NIM](https://developer.nvidia.com/blog/benchmarking-large-language-model-inference-performance-with-genai-perf-and-nim/)
17.  [nlpcloud Blog on LLM Inference Options (TensorRT-LLM, vLLM, TGI, LMDeploy)](https://nlpcloud.com/blog/llm-inference-options-tensorrt-llm-vllm-tgi-lmdeploy)
18.  [Reddit Discussion on Qwen 3 Benchmarks (various hardware/frameworks)](https://www.reddit.com/r/LocalLLaMA/comments/1cpxq4y/qwen_3_benchmarks_mlx_gguf_vllm_pytorch_tensorrt/)
19.  [Cerebrium Blog on Llama 3.1 70B FP8 Benchmarks (vLLM, SGLang, TensorRT-LLM)](https://www.cerebrium.com/blog/llama-3-1-70b-fp8-benchmarks)
20.  [vLLM GitHub Issue/Discussion on CPU Support (#1058)](https://github.com/vllm-project/vllm/issues/1058)
21.  [Hugging Face Blog on Text Generation Inference with AMD Instinct GPUs](https://huggingface.co/blog/text-generation-inference-amd)
22.  [TensorRT-LLM GitHub Documentation (Main Docs Folder)](https://github.com/NVIDIA/TensorRT-LLM/tree/main/docs)
23.  [FasterTransformer GitHub Documentation (Main Docs Folder)](https://github.com/NVIDIA/FasterTransformer/tree/main/docs)
24.  [DeepSpeed Documentation on Supported Hardware Requirements](https://www.deepspeed.ai/getting-started/#hardware-requirements)
25.  [Intel Blog on vLLM Support for Intel GPUs](https://www.intel.com/content/www/us/en/developer/articles/technical/vllm-support-for-intel-gpus.html)
26.  [Hugging Face Blog on Native Intel Gaudi Integration in TGI](https://huggingface.co/blog/tgi-gaudi)
27.  [Dell Blog on AI Platform with Intel Gaudi 3](https://www.dell.com/en-us/blog/accelerating-ai-outcomes-with-new-dell-poweredge-xe9680l/)
28.  [Red Hat Blog on Enhanced LLM Inference with OpenShift AI and Intel (mentions FasterTransformer extending capabilities)](https://www.redhat.com/en/blog/enhanced-llm-inference-red-hat-openshift-ai-and-intel-ai-software)
29.  [Intel Neural Compressor Documentation](https://www.intel.com/content/www/us/en/developer/tools/oneapi/neural-compressor.html)
30.  [arXiv Paper on FlightLLM: Efficient LLM Inference on FPGAs (2402.15260)](https://arxiv.org/abs/2402.15260)
31.  [Reddit Discussion on FPGAs for LLM Inference: Thoughts on Economics](https://www.reddit.com/r/FPGA/comments/1b4k6l8/fpga_for_llm_inference_thoughts_on_economics/)
32.  [vLLM Documentation on Deploying with BentoML](https://docs.vllm.ai/en/latest/serving/deploy_with_bentoml.html)
33.  [vLLM Documentation on Deploying with Triton](https://docs.vllm.ai/en/latest/serving/deploy_with_triton.html)
34.  [TensorRT-LLM GitHub Repository (Triton Backend)](https://github.com/triton-inference-server/tensorrtllm_backend)
35.  [FasterTransformer GitHub Documentation (GPT Guide)](https://github.com/NVIDIA/FasterTransformer/blob/main/docs/gpt_guide.md)
36.  [FasterTransformer GitHub Documentation (TensorRT Backend for Triton)](https://github.com/NVIDIA/FasterTransformer/blob/main/docs/tensorrt_backend_triton.md)
37.  [OpenLLM GitHub Repository (bentoml/OpenLLM)](https://github.com/bentoml/OpenLLM)
38.  [DeepSpeed Examples GitHub (DeepSpeed-Chat Inference)](https://github.com/microsoft/DeepSpeedExamples/tree/master/applications/DeepSpeed-Chat/inference)
39.  [arXiv Paper: A Survey on Speculative Decoding (2401.11645 - ACL 2024 Findings)](https://arxiv.org/abs/2401.11645)
40.  [Huawei Article on 6G and AI as a Service (Federated Learning Context)](https://www.huawei.com/en/news/2024/3/6g-ai-service-federated-learning)
41.  [Bud Ecosys Blog on Heterogeneous Inferencing: Making LLMs Affordable](https://budecosystem.com/blog/heterogeneous-inferencing-making-llms-affordable/)
42.  [PromptLayer Blog on AutoGen vs LangChain](https://promptlayer.com/blog/autogen-vs-langchain)
43.  [arXiv Paper on PowerInfer: Fast Large Language Model Serving with a Consumer-grade GPU (2312.12456)](https://arxiv.org/abs/2312.12456)
44.  [DEV Community Article on SGLang: A New Framework for Fast and Expressive LLM Inference](https://dev.to/vikram_makkar/sglang-a-new-framework-for-fast-and-expressive-llm-inference-2a8p)
45.  [LMDeploy Documentation (ReadTheDocs)](https://lmdeploy.readthedocs.io/en/latest/)
46.  [LMDeploy GitHub Repository (InternLM/lmdeploy)](https://github.com/InternLM/lmdeploy)
47.  [Reddit Thread on Llama 3 70B Inference Performance Comparison](https://www.reddit.com/r/LocalLLaMA/comments/1cjc758/llama_3_70b_inference_performance_comparison/)
48.  [Hugging Face Documentation on Optimized Transformer Models for Intel Gaudi (Optimum Habana Overview)](https://huggingface.co/docs/optimum/habana/overview)
49.  [Intel's xFasterTransformer GitHub Repository (CPU-focused fork)](https://github.com/intel/xFasterTransformer)
50.  [vLLM GitHub Discussions on KServe Integration (#1422)](https://github.com/vllm-project/vllm/discussions/1422)
51.  [LMDeploy GitHub Issue on KServe/BentoML Support (Community discussion #829)](https://github.com/InternLM/lmdeploy/issues/829)
52.  [SGLang GitHub Repository (sgl-project/sglang)](https://github.com/sgl-project/sglang)
53.  [arXiv Paper: Taming the Titans: A Survey of Efficient LLM Inference Serving (2310.04603)](https://arxiv.org/abs/2310.04603)
54.  [BentoML Documentation (Main Page)](https://docs.bentoml.com/)
55.  [MMDeploy Documentation on ONNX Runtime Backend](https://mmdeploy.readthedocs.io/en/latest/backends/onnxruntime.html)
56.  [MMDeploy Documentation on How to Add Custom Ops for ORT Backend](https://mmdeploy.readthedocs.io/en/latest/tutorials/how_to_add_custom_ops_for_ort_backend.html)
57.  [Hugging Face Blog on Optimizing LLMs on Gaudi2 with TGI and Optimum Habana](https://huggingface.co/blog/intel-gaudi-tgi-optimum)
58.  [Baseten Blog on Multi-LoRA Serving with TensorRT-LLM](https://www.baseten.co/blog/multi-lora-serving-with-tensorrt-llm/)
59.  [Towards Data Science Article on Navigating LLM Inference Engines (TGI, vLLM, and TensorRT-LLM)](https://towardsdatascience.com/navigating-llm-inference-engines-tgi-vllm-and-tensorrt-llm-6495101d710f)
60.  [Baseten Blog on H100 vs A100 with TensorRT-LLM for Llama 2 70B](https://www.baseten.co/blog/h100-vs-a100-tensorrt-llm-llama-2-70b/)
61.  [arXiv Paper: LLM-Inference-Bench: Inference Benchmarking of Large Language Models on AI Accelerators (2405.09010 - Abstract/Overview)](https://arxiv.org/abs/2405.09010)
62.  [NVIDIA GenAI-Perf GitHub Repository](https://github.com/NVIDIA/genai-perf)
63.  [NVIDIA NIM Documentation](https://docs.nvidia.com/nim/)
64.  [LinkedIn Article: Literature Review of LLM-Inference-Bench](https://www.linkedin.com/pulse/literature-review-llm-inference-bench-large-language-ai-khan-gmj4c)
65.  [Hyperbolic Blog on SGLang (Mentions CPU scheduler)](https://hyperbolic.xyz/blog/sglang)
66.  [SGLang GitHub Discussions (General search for CPU)](https://github.com/sgl-project/sglang/discussions)
67.  [LMDeploy GitHub Issues (General Issues Page)](https://github.com/InternLM/lmdeploy/issues)