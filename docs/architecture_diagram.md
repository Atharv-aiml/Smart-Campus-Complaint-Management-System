# System Architecture Diagram: Smart Campus Complaint Management System

```mermaid
graph TD
    subgraph Presentation_Layer ["Presentation Layer (Swing GUI)"]
        UI_MAIN["main.Main"]
        UI_LOGIN["ui.LoginFrame"]
        UI_REG["ui.RegisterDialog"]
        UI_STU_DASH["ui.StudentDashboardFrame"]
        UI_ADM_DASH["ui.AdminDashboardFrame"]
        UI_DETAILS["ui.ComplaintDetailsDialog"]
        UI_ANALYTICS["ui.AnalyticsPanel"]
        UI_THEME["util.UITheme"]
    end

    subgraph Business_Logic_Layer ["Service / Business Logic Layer"]
        SVC_AUTH["service.AuthService"]
        SVC_COMP["service.ComplaintService"]
        SVC_REP["service.ReportService"]
        UTIL_VAL["util.InputValidator"]
        UTIL_DT["util.DateTimeUtil"]
    end

    subgraph Data_Access_Layer ["Repository / Data Access Layer"]
        REPO_IFACE["repository.Repository&lt;T, ID&gt;"]
        REPO_FILE["repository.FileRepository&lt;T, ID&gt;"]
        REPO_STU["repository.StudentRepository"]
        REPO_ADM["repository.AdminRepository"]
        REPO_COMP["repository.ComplaintRepository"]
    end

    subgraph Domain_Model_Layer ["Domain Model Layer"]
        MODEL_USER["model.User (Abstract)"]
        MODEL_STU["model.Student"]
        MODEL_ADM["model.Admin"]
        MODEL_COMP["model.Complaint"]
        ENUM_ROLE["model.UserRole"]
        ENUM_CAT["model.ComplaintCategory"]
        ENUM_PRI["model.ComplaintPriority"]
        ENUM_STAT["model.ComplaintStatus"]
    end

    subgraph Persistence_Layer ["Persistent File Storage"]
        FILE_STU[("data/students.dat")]
        FILE_ADM[("data/admins.dat")]
        FILE_COMP[("data/complaints.dat")]
    end

    %% UI to Service
    UI_LOGIN --> SVC_AUTH
    UI_REG --> SVC_AUTH
    UI_STU_DASH --> SVC_COMP
    UI_STU_DASH --> SVC_REP
    UI_STU_DASH --> SVC_AUTH
    UI_ADM_DASH --> SVC_COMP
    UI_ADM_DASH --> SVC_REP
    UI_ADM_DASH --> SVC_AUTH
    UI_ANALYTICS --> SVC_REP

    %% Services to Repositories & Utils
    SVC_AUTH --> REPO_STU
    SVC_AUTH --> REPO_ADM
    SVC_AUTH --> UTIL_VAL
    SVC_COMP --> REPO_COMP
    SVC_COMP --> REPO_STU
    SVC_COMP --> UTIL_VAL
    SVC_REP --> REPO_COMP

    %% Repository Hierarchy
    REPO_STU --|> REPO_FILE
    REPO_ADM --|> REPO_FILE
    REPO_COMP --|> REPO_FILE
    REPO_FILE ..|> REPO_IFACE

    %% Models
    MODEL_STU --|> MODEL_USER
    MODEL_ADM --|> MODEL_USER
    MODEL_COMP --> ENUM_CAT
    MODEL_COMP --> ENUM_PRI
    MODEL_COMP --> ENUM_STAT
    MODEL_USER --> ENUM_ROLE

    %% Repositories to Files
    REPO_STU --> FILE_STU
    REPO_ADM --> FILE_ADM
    REPO_COMP --> FILE_COMP
```

### Layer Descriptions
1. **Presentation Layer (`ui`)**: Implements clean, professional Java Swing components for multi-role workflows. Keeps UI logic completely detached from data persistence.
2. **Service Layer (`service`)**: Encapsulates core business rules, sequential ID generation, status transition validation, and metric calculation.
3. **Repository Layer (`repository`)**: Provides clean generic interfaces and file-based object serialization with atomic file flushing to ensure data integrity.
4. **Domain Model Layer (`model`)**: Defines rich Object-Oriented representations utilizing inheritance, encapsulation, and strongly-typed enums.
5. **Persistence Layer (`data`)**: Binary serialized files storing student, administrator, and complaint records without requiring external database servers.
