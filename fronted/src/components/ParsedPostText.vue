<template>
  <view
    class="parsed-text"
    :class="[
      multiline ? 'is-multiline' : 'is-inline',
      maxLines ? 'is-truncated' : '',
    ]"
    :style="containerStyle"
  >
    <template v-if="multiline">
      <view
        v-for="(line, lineIndex) in parsedLines"
        :key="`line-${lineIndex}`"
        class="parsed-line"
      >
        <text
          v-for="(token, tokenIndex) in line"
          :key="`token-${lineIndex}-${tokenIndex}`"
          class="parsed-segment"
          :class="token.type"
          @tap.stop="handleTokenTap(token)"
        >
          {{ token.value }}
        </text>
      </view>
    </template>

    <template v-else>
      <text
        v-for="(token, tokenIndex) in inlineTokens"
        :key="`inline-${tokenIndex}`"
        class="parsed-segment"
        :class="token.type"
        @tap.stop="handleTokenTap(token)"
      >
        {{ token.value }}
      </text>
    </template>
  </view>
</template>

<script setup>
import { computed } from "vue";

const props = defineProps({
  content: {
    type: String,
    default: "",
  },
  multiline: {
    type: Boolean,
    default: false,
  },
  maxLines: {
    type: Number,
    default: 0,
  },
});

const emit = defineEmits(["mention", "tag"]);

const tokenPattern = /(@[\u4e00-\u9fa5\w-]+|#[\u4e00-\u9fa5\w-]+)/g;

function parseLine(line) {
  const tokens = [];
  let lastIndex = 0;

  line.replace(tokenPattern, (match, _, offset) => {
    if (offset > lastIndex) {
      tokens.push({
        type: "text",
        value: line.slice(lastIndex, offset),
      });
    }

    tokens.push({
      type: match.startsWith("@") ? "mention" : "tag",
      value: match,
    });
    lastIndex = offset + match.length;
    return match;
  });

  if (lastIndex < line.length) {
    tokens.push({
      type: "text",
      value: line.slice(lastIndex),
    });
  }

  return tokens.length
    ? tokens
    : [
        {
          type: "text",
          value: line,
        },
      ];
}

const parsedLines = computed(() =>
  String(props.content || "")
    .split("\n")
    .map((line) => parseLine(line)),
);

const inlineTokens = computed(() => parseLine(String(props.content || "").replace(/\n/g, " ")));

const containerStyle = computed(() =>
  props.maxLines
    ? {
        maxHeight: `${props.maxLines * 1.7}em`,
      }
    : {},
);

function handleTokenTap(token) {
  if (token.type === "mention") {
    emit("mention", token.value.slice(1));
  }

  if (token.type === "tag") {
    emit("tag", token.value.slice(1));
  }
}
</script>

<style scoped>
.parsed-text {
  word-break: break-all;
}

.is-truncated {
  overflow: hidden;
}

.parsed-line {
  display: block;
}

.parsed-segment {
  display: inline;
}

.mention,
.tag {
  color: #ff5a35;
  font-weight: 600;
}
</style>
